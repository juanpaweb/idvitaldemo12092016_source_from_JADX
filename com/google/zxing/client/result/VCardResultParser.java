package com.google.zxing.client.result;

import com.google.zxing.Result;
import idvital1.idvital1.C0239R;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VCardResultParser extends ResultParser {
    private static final Pattern BEGIN_VCARD;
    private static final Pattern COMMA;
    private static final Pattern CR_LF_SPACE_TAB;
    private static final Pattern EQUALS;
    private static final Pattern NEWLINE_ESCAPE;
    private static final Pattern SEMICOLON;
    private static final Pattern SEMICOLON_OR_COMMA;
    private static final Pattern UNESCAPED_SEMICOLONS;
    private static final Pattern VCARD_ESCAPES;
    private static final Pattern VCARD_LIKE_DATE;

    static {
        BEGIN_VCARD = Pattern.compile("BEGIN:VCARD", 2);
        VCARD_LIKE_DATE = Pattern.compile("\\d{4}-?\\d{2}-?\\d{2}");
        CR_LF_SPACE_TAB = Pattern.compile("\r\n[ \t]");
        NEWLINE_ESCAPE = Pattern.compile("\\\\[nN]");
        VCARD_ESCAPES = Pattern.compile("\\\\([,;\\\\])");
        EQUALS = Pattern.compile("=");
        SEMICOLON = Pattern.compile(";");
        UNESCAPED_SEMICOLONS = Pattern.compile("(?<!\\\\);+");
        COMMA = Pattern.compile(",");
        SEMICOLON_OR_COMMA = Pattern.compile("[;,]");
    }

    public AddressBookParsedResult parse(Result result) {
        String rawText = ResultParser.getMassagedText(result);
        Matcher m = BEGIN_VCARD.matcher(rawText);
        if (!m.find() || m.start() != 0) {
            return null;
        }
        String[] nicknames;
        List<List<String>> names = matchVCardPrefixedField("FN", rawText, true, false);
        if (names == null) {
            names = matchVCardPrefixedField("N", rawText, true, false);
            formatNames(names);
        }
        List<String> nicknameString = matchSingleVCardPrefixedField("NICKNAME", rawText, true, false);
        if (nicknameString == null) {
            nicknames = null;
        } else {
            nicknames = COMMA.split((CharSequence) nicknameString.get(0));
        }
        List<List<String>> phoneNumbers = matchVCardPrefixedField("TEL", rawText, true, false);
        List<List<String>> emails = matchVCardPrefixedField("EMAIL", rawText, true, false);
        List<String> note = matchSingleVCardPrefixedField("NOTE", rawText, false, false);
        List<List<String>> addresses = matchVCardPrefixedField("ADR", rawText, true, true);
        List<String> org = matchSingleVCardPrefixedField("ORG", rawText, true, true);
        List<String> birthday = matchSingleVCardPrefixedField("BDAY", rawText, true, false);
        if (!(birthday == null || isLikeVCardDate((CharSequence) birthday.get(0)))) {
            birthday = null;
        }
        List<String> title = matchSingleVCardPrefixedField("TITLE", rawText, true, false);
        List<List<String>> urls = matchVCardPrefixedField("URL", rawText, true, false);
        List<String> instantMessenger = matchSingleVCardPrefixedField("IMPP", rawText, true, false);
        List<String> geoString = matchSingleVCardPrefixedField("GEO", rawText, true, false);
        String[] geo = geoString == null ? null : SEMICOLON_OR_COMMA.split((CharSequence) geoString.get(0));
        if (!(geo == null || geo.length == 2)) {
            geo = null;
        }
        return new AddressBookParsedResult(toPrimaryValues(names), nicknames, null, toPrimaryValues(phoneNumbers), toTypes(phoneNumbers), toPrimaryValues(emails), toTypes(emails), toPrimaryValue(instantMessenger), toPrimaryValue(note), toPrimaryValues(addresses), toTypes(addresses), toPrimaryValue(org), toPrimaryValue(birthday), toPrimaryValue(title), toPrimaryValues(urls), geo);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.List<java.util.List<java.lang.String>> matchVCardPrefixedField(java.lang.CharSequence r22, java.lang.String r23, boolean r24, boolean r25) {
        /*
        r8 = 0;
        r3 = 0;
        r9 = r23.length();
    L_0x0006:
        if (r3 >= r9) goto L_0x003d;
    L_0x0008:
        r17 = new java.lang.StringBuilder;
        r17.<init>();
        r18 = "(?:^|\n)";
        r17 = r17.append(r18);
        r0 = r17;
        r1 = r22;
        r17 = r0.append(r1);
        r18 = "(?:;([^:]*))?:";
        r17 = r17.append(r18);
        r17 = r17.toString();
        r18 = 2;
        r17 = java.util.regex.Pattern.compile(r17, r18);
        r0 = r17;
        r1 = r23;
        r7 = r0.matcher(r1);
        if (r3 <= 0) goto L_0x0037;
    L_0x0035:
        r3 = r3 + -1;
    L_0x0037:
        r17 = r7.find(r3);
        if (r17 != 0) goto L_0x003e;
    L_0x003d:
        return r8;
    L_0x003e:
        r17 = 0;
        r0 = r17;
        r3 = r7.end(r0);
        r17 = 1;
        r0 = r17;
        r11 = r7.group(r0);
        r10 = 0;
        r14 = 0;
        r15 = 0;
        if (r11 == 0) goto L_0x00be;
    L_0x0053:
        r17 = SEMICOLON;
        r0 = r17;
        r18 = r0.split(r11);
        r0 = r18;
        r0 = r0.length;
        r19 = r0;
        r17 = 0;
    L_0x0062:
        r0 = r17;
        r1 = r19;
        if (r0 >= r1) goto L_0x00be;
    L_0x0068:
        r12 = r18[r17];
        if (r10 != 0) goto L_0x0075;
    L_0x006c:
        r10 = new java.util.ArrayList;
        r20 = 1;
        r0 = r20;
        r10.<init>(r0);
    L_0x0075:
        r10.add(r12);
        r20 = EQUALS;
        r21 = 2;
        r0 = r20;
        r1 = r21;
        r13 = r0.split(r12, r1);
        r0 = r13.length;
        r20 = r0;
        r21 = 1;
        r0 = r20;
        r1 = r21;
        if (r0 <= r1) goto L_0x00ae;
    L_0x008f:
        r20 = 0;
        r4 = r13[r20];
        r20 = 1;
        r16 = r13[r20];
        r20 = "ENCODING";
        r0 = r20;
        r20 = r0.equalsIgnoreCase(r4);
        if (r20 == 0) goto L_0x00b1;
    L_0x00a1:
        r20 = "QUOTED-PRINTABLE";
        r0 = r20;
        r1 = r16;
        r20 = r0.equalsIgnoreCase(r1);
        if (r20 == 0) goto L_0x00b1;
    L_0x00ad:
        r14 = 1;
    L_0x00ae:
        r17 = r17 + 1;
        goto L_0x0062;
    L_0x00b1:
        r20 = "CHARSET";
        r0 = r20;
        r20 = r0.equalsIgnoreCase(r4);
        if (r20 == 0) goto L_0x00ae;
    L_0x00bb:
        r15 = r16;
        goto L_0x00ae;
    L_0x00be:
        r6 = r3;
    L_0x00bf:
        r17 = 10;
        r0 = r23;
        r1 = r17;
        r3 = r0.indexOf(r1, r3);
        if (r3 < 0) goto L_0x0131;
    L_0x00cb:
        r17 = r23.length();
        r17 = r17 + -1;
        r0 = r17;
        if (r3 >= r0) goto L_0x00fc;
    L_0x00d5:
        r17 = r3 + 1;
        r0 = r23;
        r1 = r17;
        r17 = r0.charAt(r1);
        r18 = 32;
        r0 = r17;
        r1 = r18;
        if (r0 == r1) goto L_0x00f9;
    L_0x00e7:
        r17 = r3 + 1;
        r0 = r23;
        r1 = r17;
        r17 = r0.charAt(r1);
        r18 = 9;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x00fc;
    L_0x00f9:
        r3 = r3 + 2;
        goto L_0x00bf;
    L_0x00fc:
        if (r14 == 0) goto L_0x0131;
    L_0x00fe:
        r17 = 1;
        r0 = r17;
        if (r3 < r0) goto L_0x0116;
    L_0x0104:
        r17 = r3 + -1;
        r0 = r23;
        r1 = r17;
        r17 = r0.charAt(r1);
        r18 = 61;
        r0 = r17;
        r1 = r18;
        if (r0 == r1) goto L_0x012e;
    L_0x0116:
        r17 = 2;
        r0 = r17;
        if (r3 < r0) goto L_0x0131;
    L_0x011c:
        r17 = r3 + -2;
        r0 = r23;
        r1 = r17;
        r17 = r0.charAt(r1);
        r18 = 61;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x0131;
    L_0x012e:
        r3 = r3 + 1;
        goto L_0x00bf;
    L_0x0131:
        if (r3 >= 0) goto L_0x0136;
    L_0x0133:
        r3 = r9;
        goto L_0x0006;
    L_0x0136:
        if (r3 <= r6) goto L_0x01e2;
    L_0x0138:
        if (r8 != 0) goto L_0x0143;
    L_0x013a:
        r8 = new java.util.ArrayList;
        r17 = 1;
        r0 = r17;
        r8.<init>(r0);
    L_0x0143:
        r17 = 1;
        r0 = r17;
        if (r3 < r0) goto L_0x015d;
    L_0x0149:
        r17 = r3 + -1;
        r0 = r23;
        r1 = r17;
        r17 = r0.charAt(r1);
        r18 = 13;
        r0 = r17;
        r1 = r18;
        if (r0 != r1) goto L_0x015d;
    L_0x015b:
        r3 = r3 + -1;
    L_0x015d:
        r0 = r23;
        r2 = r0.substring(r6, r3);
        if (r24 == 0) goto L_0x0169;
    L_0x0165:
        r2 = r2.trim();
    L_0x0169:
        if (r14 == 0) goto L_0x0198;
    L_0x016b:
        r2 = decodeQuotedPrintable(r2, r15);
        if (r25 == 0) goto L_0x0183;
    L_0x0171:
        r17 = UNESCAPED_SEMICOLONS;
        r0 = r17;
        r17 = r0.matcher(r2);
        r18 = "\n";
        r17 = r17.replaceAll(r18);
        r2 = r17.trim();
    L_0x0183:
        if (r10 != 0) goto L_0x01d7;
    L_0x0185:
        r5 = new java.util.ArrayList;
        r17 = 1;
        r0 = r17;
        r5.<init>(r0);
        r5.add(r2);
        r8.add(r5);
    L_0x0194:
        r3 = r3 + 1;
        goto L_0x0006;
    L_0x0198:
        if (r25 == 0) goto L_0x01ac;
    L_0x019a:
        r17 = UNESCAPED_SEMICOLONS;
        r0 = r17;
        r17 = r0.matcher(r2);
        r18 = "\n";
        r17 = r17.replaceAll(r18);
        r2 = r17.trim();
    L_0x01ac:
        r17 = CR_LF_SPACE_TAB;
        r0 = r17;
        r17 = r0.matcher(r2);
        r18 = "";
        r2 = r17.replaceAll(r18);
        r17 = NEWLINE_ESCAPE;
        r0 = r17;
        r17 = r0.matcher(r2);
        r18 = "\n";
        r2 = r17.replaceAll(r18);
        r17 = VCARD_ESCAPES;
        r0 = r17;
        r17 = r0.matcher(r2);
        r18 = "$1";
        r2 = r17.replaceAll(r18);
        goto L_0x0183;
    L_0x01d7:
        r17 = 0;
        r0 = r17;
        r10.add(r0, r2);
        r8.add(r10);
        goto L_0x0194;
    L_0x01e2:
        r3 = r3 + 1;
        goto L_0x0006;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.result.VCardResultParser.matchVCardPrefixedField(java.lang.CharSequence, java.lang.String, boolean, boolean):java.util.List<java.util.List<java.lang.String>>");
    }

    private static String decodeQuotedPrintable(CharSequence value, String charset) {
        int length = value.length();
        StringBuilder result = new StringBuilder(length);
        ByteArrayOutputStream fragmentBuffer = new ByteArrayOutputStream();
        int i = 0;
        while (i < length) {
            char c = value.charAt(i);
            switch (c) {
                case C0239R.styleable.Toolbar_contentInsetEndWithActions /*10*/:
                case C0239R.styleable.Toolbar_subtitleTextAppearance /*13*/:
                    break;
                case C0239R.styleable.AppCompatTheme_popupMenuStyle /*61*/:
                    if (i >= length - 2) {
                        break;
                    }
                    char nextChar = value.charAt(i + 1);
                    if (!(nextChar == '\r' || nextChar == '\n')) {
                        char nextNextChar = value.charAt(i + 2);
                        int firstDigit = ResultParser.parseHexDigit(nextChar);
                        int secondDigit = ResultParser.parseHexDigit(nextNextChar);
                        if (firstDigit >= 0 && secondDigit >= 0) {
                            fragmentBuffer.write((firstDigit << 4) + secondDigit);
                        }
                        i += 2;
                        break;
                    }
                default:
                    maybeAppendFragment(fragmentBuffer, charset, result);
                    result.append(c);
                    break;
            }
            i++;
        }
        maybeAppendFragment(fragmentBuffer, charset, result);
        return result.toString();
    }

    private static void maybeAppendFragment(ByteArrayOutputStream fragmentBuffer, String charset, StringBuilder result) {
        if (fragmentBuffer.size() > 0) {
            String fragment;
            byte[] fragmentBytes = fragmentBuffer.toByteArray();
            if (charset == null) {
                fragment = new String(fragmentBytes, Charset.forName("UTF-8"));
            } else {
                try {
                    fragment = new String(fragmentBytes, charset);
                } catch (UnsupportedEncodingException e) {
                    fragment = new String(fragmentBytes, Charset.forName("UTF-8"));
                }
            }
            fragmentBuffer.reset();
            result.append(fragment);
        }
    }

    static List<String> matchSingleVCardPrefixedField(CharSequence prefix, String rawText, boolean trim, boolean parseFieldDivider) {
        List<List<String>> values = matchVCardPrefixedField(prefix, rawText, trim, parseFieldDivider);
        return (values == null || values.isEmpty()) ? null : (List) values.get(0);
    }

    private static String toPrimaryValue(List<String> list) {
        return (list == null || list.isEmpty()) ? null : (String) list.get(0);
    }

    private static String[] toPrimaryValues(Collection<List<String>> lists) {
        if (lists == null || lists.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList(lists.size());
        for (List<String> list : lists) {
            String value = (String) list.get(0);
            if (!(value == null || value.isEmpty())) {
                result.add(value);
            }
        }
        return (String[]) result.toArray(new String[lists.size()]);
    }

    private static String[] toTypes(Collection<List<String>> lists) {
        if (lists == null || lists.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList(lists.size());
        for (List<String> list : lists) {
            String type = null;
            int i = 1;
            while (i < list.size()) {
                String metadatum = (String) list.get(i);
                int equals = metadatum.indexOf(61);
                if (equals < 0) {
                    type = metadatum;
                    break;
                } else if ("TYPE".equalsIgnoreCase(metadatum.substring(0, equals))) {
                    type = metadatum.substring(equals + 1);
                    break;
                } else {
                    i++;
                }
            }
            result.add(type);
        }
        return (String[]) result.toArray(new String[lists.size()]);
    }

    private static boolean isLikeVCardDate(CharSequence value) {
        return value == null || VCARD_LIKE_DATE.matcher(value).matches();
    }

    private static void formatNames(Iterable<List<String>> names) {
        if (names != null) {
            for (List<String> list : names) {
                String name = (String) list.get(0);
                String[] components = new String[5];
                int start = 0;
                int componentIndex = 0;
                while (componentIndex < components.length - 1) {
                    int end = name.indexOf(59, start);
                    if (end < 0) {
                        break;
                    }
                    components[componentIndex] = name.substring(start, end);
                    componentIndex++;
                    start = end + 1;
                }
                components[componentIndex] = name.substring(start);
                StringBuilder newName = new StringBuilder(100);
                maybeAppendComponent(components, 3, newName);
                maybeAppendComponent(components, 1, newName);
                maybeAppendComponent(components, 2, newName);
                maybeAppendComponent(components, 0, newName);
                maybeAppendComponent(components, 4, newName);
                list.set(0, newName.toString().trim());
            }
        }
    }

    private static void maybeAppendComponent(String[] components, int i, StringBuilder newName) {
        if (components[i] != null && !components[i].isEmpty()) {
            if (newName.length() > 0) {
                newName.append(' ');
            }
            newName.append(components[i]);
        }
    }
}
