package com.example.android.basicmediadecoder;

import android.content.Context;
import android.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConvertTable {

    // sting value for vpp
    public static final String HQV_MODE_OFF = "HQV_MODE_OFF";
    public static final String HQV_MODE_AUTO = "HQV_MODE_AUTO";
    public static final String HQV_MODE_MANUAL = "HQV_MODE_MANUAL";

    public static final String QBR_MODE_OFF = "QBR_MODE_OFF";
    public static final String QBR_MODE_ON = "QBR_MODE_ON";

    public static final String HQV_HUE_MODE_OFF = "HQV_HUE_MODE_OFF";
    public static final String HQV_HUE_MODE_ON = "HQV_HUE_MODE_ON";

    public static final String FRC_MODE_OFF = "FRC_MODE_OFF";
    public static final String FRC_MODE_LOW = "FRC_MODE_LOW";
    public static final String FRC_MODE_MED = "FRC_MODE_MED";
    public static final String FRC_MODE_HIGH = "FRC_MODE_HIGH";

    public static final List<String> hqvValueList = Arrays.asList(HQV_MODE_OFF, HQV_MODE_AUTO, HQV_MODE_MANUAL);
    public static final List<String> qbrValueList = Arrays.asList(QBR_MODE_OFF, QBR_MODE_ON);
    public static final List<String> hueValueList = Arrays.asList(HQV_HUE_MODE_OFF, HQV_HUE_MODE_ON);
    public static final List<String> frcValueList = Arrays.asList(FRC_MODE_OFF, FRC_MODE_LOW, FRC_MODE_MED, FRC_MODE_HIGH);

    // keys for vpp
    public static final String VPP_MODE = "vpp.mode"; // string

    public static final String AIE_MODE = "vpp-aie.mode"; // string
    public static final String AIE_HUE_MODE = "vpp-aie.hue-mode"; // string
    public static final String AIE_CADE_LEVEL = "vpp-aie.cade-level"; // int
    public static final String AIE_LTM_LEVEL = "vpp-aie.ltm-level"; // int

    public static final String CNR_MODE = "vpp-cnr.mode"; // string
    public static final String CNR_LEVEL = "vpp-cnr.level"; // int

    public static final String QBR_MODE = "vpp-qbr.mode"; // string

    public static final String CADE_MODE = "vpp-cade.mode"; // string
    public static final String CADE_CADE_LEVEL = "vpp-cade.cade-level"; // int
    public static final String CADE_CONTRAST = "vpp-cade.contrast"; // int
    public static final String CADE_SATURATION = "vpp-cade.saturation"; // int

    public static final String FRC_MODE = "vpp-frc.mode"; // string

    public static final String VPP_PREFIX = "vendor.qti-ext-";

    // value type
    public static final int STRING_TYPE = 0;
    public static final int INT_TYPE = 1;

    private static Map<String, VppValueObject> valueObjects = new HashMap<>();
    private static boolean valueObjectsInited = false;

    static {
        initValueTypes();
    }

    public static Pair getOption(String subKey, int value) {
        Pair pair = null;
        VppValueObject obj = valueObjects.get(subKey);
        if (obj.getValueType() == STRING_TYPE) {
            pair = new Pair(VPP_PREFIX + subKey, obj.getValueList().get(value));
        } else if (obj.getValueType() == INT_TYPE) {
            pair = new Pair(VPP_PREFIX + subKey, value);
        }
        return pair;
    }

    public static int getType(String key) {
        VppValueObject obj = valueObjects.get(key);
        return obj.getValueType();
    }

    public static String getOptionString(String key, int value) {
        String str = null;
        VppValueObject obj = valueObjects.get(key);
        if (obj.getValueType() == STRING_TYPE) {
            str = obj.getValueList().get(value);
        }
        return str;
    }

    synchronized private static void initValueTypes() {
        if (!valueObjectsInited) {
            valueObjects.put(VPP_MODE, new VppValueObject(STRING_TYPE, hqvValueList, -1));

            valueObjects.put(AIE_MODE, new VppValueObject(STRING_TYPE, hqvValueList, -1));
            valueObjects.put(AIE_HUE_MODE, new VppValueObject(STRING_TYPE, hueValueList, -1));
            valueObjects.put(AIE_CADE_LEVEL, new VppValueObject(INT_TYPE, null, -1));
            valueObjects.put(AIE_LTM_LEVEL, new VppValueObject(INT_TYPE, null, -1));

            valueObjects.put(CNR_MODE, new VppValueObject(STRING_TYPE, hqvValueList, -1));
            valueObjects.put(CNR_LEVEL, new VppValueObject(INT_TYPE, null, -1));

            valueObjects.put(QBR_MODE, new VppValueObject(STRING_TYPE, qbrValueList, -1));

            valueObjects.put(CADE_MODE, new VppValueObject(STRING_TYPE, hqvValueList, -1));
            valueObjects.put(CADE_CADE_LEVEL, new VppValueObject(INT_TYPE, null, -1));
            valueObjects.put(CADE_CONTRAST, new VppValueObject(INT_TYPE, null, -1));
            valueObjects.put(CADE_SATURATION, new VppValueObject(INT_TYPE, null, -1));

            valueObjects.put(FRC_MODE, new VppValueObject(STRING_TYPE, frcValueList, -1));

            valueObjectsInited = true;
        }
    }

    public static class VppValueObject {
        private int valueType;
        private List<String> valueList;
        private int intValue = -1;

        VppValueObject(int valueType, List<String> valueList, int intValue) {
            this.valueType = valueType;
            if (valueType == STRING_TYPE) {
                this.valueList = valueList;
            } else if (valueType == INT_TYPE) {
                this.intValue = intValue;
            }
        }

        public int getValueType() {
            return valueType;
        }

        public List<String> getValueList() {
            return valueList;
        }

        public int getIntValue() {
            return intValue;
        }
    }


    public static final int LEVEL_DEFAULT = 50;
    public static final int LEVEL_MIN = 0;
    public static final int LEVEL_MAX = 100;
    public static final int CONTRAST_DEFAULT = 0;
    public static final int CONTRAST_MIN = -50;
    public static final int CONTRAST_MAX = 50;

    public static final int HQV_MODE_DEFAULT = 2;
    public static final int HQV_MODE_MIN = 0;
    public static final int HQV_MODE_MAX = 2;
    public static final int AIE_HUE_MODE_DEFAULT = 1;
    public static final int AIE_HUE_MODE_MIN = 0;
    public static final int AIE_HUE_MODE_MAX = 1;
    public static final int QBR_MODE_DEFAULT = 1;
    public static final int QBR_MODE_MIN = 0;
    public static final int QBR_MODE_MAX = 1;
    public static final int FRC_DEFAULT = 2;
    public static final int FRC_MIN = 0;
    public static final int FRC_MAX = 3;

    private static Map<String, DataBean> mMap = new LinkedHashMap<>();

    public static Map<String, DataBean> getMap() {
        return mMap;
    }

    public static void updateMap(String key, int type, int min, int max, int num, String description) {
        mMap.put(key, new DataBean(key, type, min, max, num, description));
    }

    private static String getResourceString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

    public static void disableAllVpp(Context context) {
        configureVpp(context, HQV_MODE_MIN);
        configureCade(context, HQV_MODE_MIN);
        configureQbr(context, QBR_MODE_MIN);
        configureCnr(context, HQV_MODE_MIN);
        configureAie(context, HQV_MODE_MIN, AIE_HUE_MODE_MIN);
        configureFrc(context, FRC_MIN);
    }

    public static void initDefaultSettings(Context context) {
        configureVpp(context, HQV_MODE_DEFAULT);
        configureCade(context, HQV_MODE_DEFAULT);
        configureQbr(context, QBR_MODE_DEFAULT);
        configureCnr(context, HQV_MODE_DEFAULT);
        configureAie(context, HQV_MODE_DEFAULT, AIE_HUE_MODE_DEFAULT);
        configureFrc(context, FRC_DEFAULT);
    }

    private static void configureVpp(Context context, int mode) {
        updateMap(VPP_MODE, STRING_TYPE, HQV_MODE_MIN, HQV_MODE_MAX, mode,
                getResourceString(context, R.string.vpp_mode));
    }

    private static void configureAie(Context context, int aieMode, int aieHueMode) {
        updateMap(AIE_MODE, STRING_TYPE, HQV_MODE_MIN, HQV_MODE_MAX, aieMode,
                context.getResources().getString(R.string.aie_mode));
        updateMap(AIE_HUE_MODE, STRING_TYPE, AIE_HUE_MODE_MIN, AIE_HUE_MODE_MAX, aieHueMode,
                context.getResources().getString(R.string.aie_hue_mode));
        updateMap(AIE_CADE_LEVEL, INT_TYPE, LEVEL_MIN, LEVEL_MAX, LEVEL_DEFAULT,
                context.getResources().getString(R.string.aie_cade_level));
        updateMap(AIE_LTM_LEVEL, INT_TYPE, LEVEL_MIN, LEVEL_MAX, LEVEL_DEFAULT,
                context.getResources().getString(R.string.aie_ltm_level));
    }

    private static void configureCnr(Context context, int mode) {
        updateMap(CNR_MODE, STRING_TYPE, HQV_MODE_MIN, HQV_MODE_MAX, mode,
                context.getResources().getString(R.string.cnr_mode));
        updateMap(CNR_LEVEL, INT_TYPE, LEVEL_MIN, LEVEL_MAX, LEVEL_DEFAULT,
                context.getResources().getString(R.string.cnr_level));
    }

    private static void configureQbr(Context context, int mode) {
        updateMap(QBR_MODE, STRING_TYPE, QBR_MODE_MIN, QBR_MODE_MAX, mode,
                context.getResources().getString(R.string.qbr_mode));
    }

    private static void configureCade(Context context, int mode) {
        updateMap(CADE_MODE, STRING_TYPE, HQV_MODE_MIN, HQV_MODE_MAX, mode,
                context.getResources().getString(R.string.vpp_cade_mode));
        updateMap(CADE_CADE_LEVEL, INT_TYPE, LEVEL_MIN, LEVEL_MAX, LEVEL_DEFAULT,
                context.getResources().getString(R.string.vpp_cade_level));
        updateMap(CADE_CONTRAST, INT_TYPE, CONTRAST_MIN, CONTRAST_MAX, CONTRAST_DEFAULT,
                context.getResources().getString(R.string.vpp_cade_contrast));
        updateMap(CADE_SATURATION, INT_TYPE, CONTRAST_MIN, CONTRAST_MAX, CONTRAST_DEFAULT,
                context.getResources().getString(R.string.vpp_cade_satuation));
    }

    private static void configureFrc(Context context, int mode) {
        updateMap(FRC_MODE, STRING_TYPE, FRC_MIN, FRC_MAX, mode,
                context.getResources().getString(R.string.frc_mode));
    }

}
