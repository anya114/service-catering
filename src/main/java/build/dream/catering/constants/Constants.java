package build.dream.catering.constants;

/**
 * Created by liuyandong on 2017/5/5.
 */
public class Constants extends build.dream.common.constants.Constants {
    public static final String GOODS_TABLE_TEMPLATE = "goods_table_template";
    public static final String SALE_TABLE_TEMPLATE = "sale_table_template";
    public static final String ELEME_APP_ID = "eleme.app.id";
    public static final String ELEME_APP_KEY = "eleme.app.key";
    public static final String ELEME_APP_SECRET = "eleme.app.secret";
    public static final String ELEME_SERVICE_URL = "eleme.service.url";
    public static final String KEY_ELEME_TOKEN = "_eleme_token";
    public static final String ELEME_TENANT_AUTHORIZE_URL_FORMAT = "%s?response_type=%s&client_id=%s&redirect_uri=%s&state=%s&scope=%s";
    public static final String ELEME_ORDER_CALLBACK_SUCCESS_RETURN_VALUE = "{\"message\":\"ok\"}";

    public static final String MEI_TUAN_APP_ID = "mei.tuan.app.id";
    public static final String MEI_TUAN_CONSUMER_SECRET = "mei.tuan.consumer.secret";
    public static final String MEI_TUAN_SERVICE_URL = "mei.tuan.service.url";
    public static final String MEI_TUAN_ERP_SERVICE_URL = "mei.tuan.erp.service.url";
    public static final String MEI_TUAN_DEVELOPER_ID = "mei.tuan.developer.id";
    public static final String MEI_TUAN_SIGN_KEY = "mei.tuan.sign.key";
    public static final String MEI_TUAN_STORE_MAP_URI = "/storemap";

    public static final String KEY_MEI_TUAN_APP_AUTH_TOKENS = "_mei_tuan_app_auth_tokens";
    public static final String MEI_MEI_TUAN_SERVICE_URL = "http://api.open.cater.meituan.com";
    public static final String MEI_TUAN_CALLBACK_SUCCESS_RETURN_VALUE = "{\"data\": \"OK\"}";

    public static final String DATA_TYPE_DIET_ORDER = "dietOrder";
    public static final String DIET_ORDER = "diet_order";

    public static final String DATA_JOB_CRON_EXPRESSION = "data.job.cron.expression";

    public static Integer GOODS_TYPE_ORDINARY_GOODS = 1;
    public static Integer GOODS_TYPE_PACKAGE = 2;

    public static final String KEY_DATA_HANDLE_SIGNATURES = "_data_handle_signatures";

    public static final String GOODS_SPECIFICATION_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/goodsSpecificationInfosSchema.json";
    public static final String FLAVOR_GROUP_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/flavorGroupInfosSchema.json";
    public static final String BUY_GIVE_ACTIVITY_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/buyGiveActivityInfosSchema.json";
    public static final String SPECIAL_GOODS_ACTIVITY_INFOS_SCHEMA_FILE_PATH = "build/dream/catering/schemas/specialGoodsActivityInfosSchema.json";
    public static final String ELEME_MESSAGE_SCHEMA_FILE_PATH = "build/dream/catering/schemas/elemeMessageSchema.json";

    public static final String KEY_BUY_GIVE_ACTIVITIES = "_buy_give_activities";
    public static final String KEY_FULL_REDUCTION_ACTIVITIES = "_full_reduction_activities";
    public static final String KEY_SPECIAL_GOODS_ACTIVITIES = "_special_goods_activities";

    public static final String ANUBIS_SERVICE_URL = "anubis.service.url";
    public static final String ANUBIS_APP_ID = "anubis.app.id";
    public static final String ANUBIS_APP_SECRET = "anubis.app.secret";

    public static final String ANUBIS_GET_ACCESS_TOKEN_URI = "/get_access_token";
    public static final String ANUBIS_CHAIN_STORE_URI = "/v2/chain_store";
    public static final String ANUBIS_CHAIN_STORE_QUERY_URI = "/v2/chain_store/query";
    public static final String ANUBIS_CHAIN_STORE_UPDATE_URI = "/v2/chain_store/update";
    public static final String ANUBIS_CHAIN_STORE_DELIVERY_QUERY_URI = "/v2/chain_store/delivery/query";
    public static final String ANUBIS_ORDER_CARRIER_URI = "/v2/order/carrier";
    public static final String ANUBIS_ORDER_URI = "/v2/order";
    public static final String ANUBIS_ORDER_CANCEL_URI = "/v2/order/cancel";
    public static final String ANUBIS_ORDER_QUERY_URI = "/v2/order/query";
    public static final String ANUBIS_ORDER_COMPLAINT_URI = "/v2/order/complaint";

    public static final String KEY_ANUBIS_TOKEN = "_anubis_token";
    public static Integer POSITION_SOURCE_TENCENT_MAP = 1;
    public static Integer POSITION_SOURCE_BAIDU_MAP = 2;
    public static Integer POSITION_SOURCE_GAODE_MAP = 3;

    public static final String KEY_DINGTALK_TOKEN = "_dingtalk_token";
    public static final String DINGTALK_CORP_ID = "dingtalk.corp.id";
    public static final String DINGTALK_CORP_SECRET = "dingtalk.corp.secret";
    public static final String DINGTALK_SERVICE_URL = "dingtalk.service.url";
    public static final String DINGTALK_GET_TOKEN_URI = "/gettoken";
    public static final String DINGTALK_CHAT_SEND_URI = "/chat/send";
    public static final String DINGTALK_SENDER = "dingtalk.sender";
    public static final String DINGTALK_CHAT_ID = "dingtalk.chat.id";

    public static final String DINGTALK_ERROR_MESSAGE_FORMAT = "%s-%s-%s-%s";

    public static final String MESSAGE_CODE_ELEME_MESSAGE = "1001";
    public static final String MESSAGE_CODE_MEI_TUAN_MESSAGE = "2001";
    public static final String MESSAGE_CODE_UPLOAD_LOG = "3001";
    public static final String MESSAGE_CODE_UPLOAD_DATABASE = "4001";
}
