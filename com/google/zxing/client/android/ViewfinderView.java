package com.google.zxing.client.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.View;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.camera.CameraManager;
import java.util.ArrayList;
import java.util.List;

public final class ViewfinderView extends View {
    private static final long ANIMATION_DELAY = 80;
    private static final int CURRENT_POINT_OPACITY = 160;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;
    private static final int[] SCANNER_ALPHA;
    private CameraManager cameraManager;
    private final int laserColor;
    private List<ResultPoint> lastPossibleResultPoints;
    private final int maskColor;
    private final Paint paint;
    private List<ResultPoint> possibleResultPoints;
    private Bitmap resultBitmap;
    private final int resultColor;
    private final int resultPointColor;
    private int scannerAlpha;

    static {
        SCANNER_ALPHA = new int[]{0, 64, TransportMediator.FLAG_KEY_MEDIA_NEXT, 192, MotionEventCompat.ACTION_MASK, 192, TransportMediator.FLAG_KEY_MEDIA_NEXT, 64};
    }

    public ViewfinderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint(1);
        Resources resources = getResources();
        this.maskColor = resources.getColor(C0223R.color.zxing_viewfinder_mask);
        this.resultColor = resources.getColor(C0223R.color.zxing_result_view);
        this.laserColor = resources.getColor(C0223R.color.zxing_viewfinder_laser);
        this.resultPointColor = resources.getColor(C0223R.color.zxing_possible_result_points);
        this.scannerAlpha = 0;
        this.possibleResultPoints = new ArrayList(5);
        this.lastPossibleResultPoints = null;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @SuppressLint({"DrawAllocation"})
    public void onDraw(Canvas canvas) {
        if (this.cameraManager != null) {
            Rect frame = this.cameraManager.getFramingRect();
            Rect previewFrame = this.cameraManager.getFramingRectInPreview();
            if (frame != null && previewFrame != null) {
                int i;
                int width = canvas.getWidth();
                int height = canvas.getHeight();
                Paint paint = this.paint;
                if (this.resultBitmap != null) {
                    i = this.resultColor;
                } else {
                    i = this.maskColor;
                }
                paint.setColor(i);
                canvas.drawRect(0.0f, 0.0f, (float) width, (float) frame.top, this.paint);
                canvas.drawRect(0.0f, (float) frame.top, (float) frame.left, (float) (frame.bottom + 1), this.paint);
                canvas.drawRect((float) (frame.right + 1), (float) frame.top, (float) width, (float) (frame.bottom + 1), this.paint);
                canvas.drawRect(0.0f, (float) (frame.bottom + 1), (float) width, (float) height, this.paint);
                if (this.resultBitmap != null) {
                    this.paint.setAlpha(CURRENT_POINT_OPACITY);
                    canvas.drawBitmap(this.resultBitmap, null, frame, this.paint);
                    return;
                }
                this.paint.setColor(this.laserColor);
                this.paint.setAlpha(SCANNER_ALPHA[this.scannerAlpha]);
                this.scannerAlpha = (this.scannerAlpha + 1) % SCANNER_ALPHA.length;
                int middle = (frame.height() / 2) + frame.top;
                canvas.drawRect((float) (frame.left + 2), (float) (middle - 1), (float) (frame.right - 1), (float) (middle + 2), this.paint);
                float scaleX = ((float) frame.width()) / ((float) previewFrame.width());
                float scaleY = ((float) frame.height()) / ((float) previewFrame.height());
                List<ResultPoint> currentPossible = this.possibleResultPoints;
                List<ResultPoint> currentLast = this.lastPossibleResultPoints;
                int frameLeft = frame.left;
                int frameTop = frame.top;
                if (currentPossible.isEmpty()) {
                    this.lastPossibleResultPoints = null;
                } else {
                    this.possibleResultPoints = new ArrayList(5);
                    this.lastPossibleResultPoints = currentPossible;
                    this.paint.setAlpha(CURRENT_POINT_OPACITY);
                    this.paint.setColor(this.resultPointColor);
                    synchronized (currentPossible) {
                        for (ResultPoint point : currentPossible) {
                            canvas.drawCircle((float) (((int) (point.getX() * scaleX)) + frameLeft), (float) (((int) (point.getY() * scaleY)) + frameTop), 6.0f, this.paint);
                        }
                    }
                }
                if (currentLast != null) {
                    this.paint.setAlpha(80);
                    this.paint.setColor(this.resultPointColor);
                    synchronized (currentLast) {
                        for (ResultPoint point2 : currentLast) {
                            canvas.drawCircle((float) (((int) (point2.getX() * scaleX)) + frameLeft), (float) (((int) (point2.getY() * scaleY)) + frameTop), 3.0f, this.paint);
                        }
                    }
                }
                postInvalidateDelayed(ANIMATION_DELAY, frame.left - 6, frame.top - 6, frame.right + POINT_SIZE, frame.bottom + POINT_SIZE);
            }
        }
    }

    public void drawViewfinder() {
        Bitmap resultBitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (resultBitmap != null) {
            resultBitmap.recycle();
        }
        invalidate();
    }

    public void drawResultBitmap(Bitmap barcode) {
        this.resultBitmap = barcode;
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = this.possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                points.subList(0, size - 10).clear();
            }
        }
    }
}
