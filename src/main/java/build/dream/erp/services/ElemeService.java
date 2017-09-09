package build.dream.erp.services;

import build.dream.common.api.ApiRest;
import build.dream.common.constants.ElemeOrderConstants;
import build.dream.common.erp.domains.*;
import build.dream.common.utils.*;
import build.dream.erp.constants.Constants;
import build.dream.erp.mappers.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ElemeService {
    @Autowired
    private BranchMapper branchMapper;
    @Autowired
    private ElemeOrderMapper elemeOrderMapper;
    @Autowired
    private ElemeGroupMapper elemeGroupMapper;
    @Autowired
    private ElemeItemMapper elemeItemMapper;
    @Autowired
    private ElemeItemAttributeMapper elemeItemAttributeMapper;
    @Autowired
    private ElemeItemNewSpecMapper elemeItemNewSpecMapper;
    @Autowired
    private ElemeActivityMapper elemeActivityMapper;
    @Autowired
    private ElemeRefundOrderMessageMapper elemeRefundOrderMessageMapper;
    @Autowired
    private DietOrderMapper dietOrderMapper;

    @Transactional(readOnly = true)
    public ApiRest tenantAuthorize(BigInteger tenantId, BigInteger branchId) throws IOException {
        SearchModel searchModel = new SearchModel(true);
        searchModel.addSearchCondition("tenant_id", "=", tenantId);
        searchModel.addSearchCondition("id", "=", branchId);
        Branch branch = branchMapper.find(searchModel);
        Validate.notNull(branch, "门店不存在！");
        Map<String, String> checkIsAuthorizeRequestParameters = new HashMap<String, String>();
        checkIsAuthorizeRequestParameters.put("tenantId", tenantId.toString());
        checkIsAuthorizeRequestParameters.put("branchId", branchId.toString());
        checkIsAuthorizeRequestParameters.put("branchType", branch.getType().toString());
        String checkIsAuthorizeResult = ProxyUtils.doGetWithRequestParameters(Constants.SERVICE_NAME_OUT, "eleme", "checkIsAuthorize", checkIsAuthorizeRequestParameters);
        ApiRest checkIsAuthorizeApiRest = ApiRest.fromJson(checkIsAuthorizeResult);
        Validate.isTrue(checkIsAuthorizeApiRest.isSuccessful(), checkIsAuthorizeApiRest.getError());
        boolean isAuthorize = (boolean) checkIsAuthorizeApiRest.getData();

        String data = null;
        if (isAuthorize) {
            String serviceName = ConfigurationUtils.getConfiguration(Constants.SERVICE_NAME);
            data = SystemPartitionUtils.getOutsideServiceDomain(serviceName) + "/eleme/bindingRestaurant?tenantId=" + tenantId + "&branchId=" + branchId;
        } else {
            String elemeUrl = ConfigurationUtils.getConfiguration(Constants.ELEME_URL);
            String elemeAppKey = ConfigurationUtils.getConfiguration(Constants.ELEME_APP_KEY);

            String outServiceOutsideServiceDomain = SystemPartitionUtils.getOutsideServiceDomain(Constants.SERVICE_NAME_OUT);
            data = String.format(Constants.ELEME_TENANT_AUTHORIZE_URL_FORMAT, elemeUrl + "/" + "authorize", "code", elemeAppKey, URLEncoder.encode(outServiceOutsideServiceDomain + "/eleme/tenantAuthorizeCallback", Constants.CHARSET_UTF_8), tenantId + "Z" + branchId, "all");
        }
        ApiRest apiRest = new ApiRest(data, "生成授权链接成功！");
        return apiRest;
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveElemeOrder(BigInteger shopId, JSONObject message, Integer type) throws IOException {
        ApiRest apiRest = null;
        String orderId = message.optString("id");
        String key = "_eleme_order_callback_" + orderId + "_" + type;
        try {
            Long returnValue = CacheUtils.setnx(key, key);
            if (returnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("保存订单成功！");
                apiRest.setSuccessful(true);
            } else {
                CacheUtils.expire(key, 30 * 60);
                JSONArray phoneList = message.optJSONArray("phoneList");
                message.remove("phoneList");

                JSONArray elemeGroupJsonArray = message.optJSONArray("groups");
                message.remove("groups");

                JSONArray orderActivityJsonArray = message.optJSONArray("orderActivities");
                message.remove("orderActivities");

                BigInteger userId = CommonUtils.getServiceSystemUserId();
                ElemeOrder elemeOrder = GsonUtils.fromJson(message.toString(), ElemeOrder.class, "yyyy-MM-dd'T'HH:mm:ss");
                elemeOrder.setCreateUserId(userId);
                elemeOrder.setLastUpdateUserId(userId);
                elemeOrder.setLastUpdateRemark("饿了么系统推送新订单，保存订单！");
                elemeOrderMapper.insert(elemeOrder);

                int elemeGroupJsonArraySize = elemeGroupJsonArray.size();
                for (int index = 0; index < elemeGroupJsonArraySize; index++) {
                    JSONObject elemeGroupJsonObject = elemeGroupJsonArray.getJSONObject(index);
                    ElemeGroup elemeGroup = new ElemeGroup();
                    elemeGroup.setOrderId(orderId);
                    elemeGroup.setElemeOrderId(elemeOrder.getId());
                    elemeGroup.setName(elemeGroupJsonObject.optString("name"));
                    elemeGroup.setType(elemeGroupJsonObject.optString("type"));
                    elemeGroup.setCreateUserId(userId);
                    elemeGroup.setLastUpdateUserId(userId);
                    elemeGroup.setLastUpdateRemark("饿了么系统推送新订单，保存分组");
                    elemeGroupMapper.insert(elemeGroup);
                    JSONArray elemeItemJsonArray = elemeGroupJsonObject.optJSONArray("items");
                    int elemeItemJsonArraySize = elemeItemJsonArray.size();
                    for (int elemeItemJsonArrayIndex = 0; elemeItemJsonArrayIndex < elemeItemJsonArraySize; elemeItemJsonArrayIndex++) {
                        JSONObject elemeItemJsonObject = elemeItemJsonArray.optJSONObject(elemeItemJsonArrayIndex);
                        ElemeItem elemeItem = new ElemeItem();
                        elemeItem.setElemeGroupId(elemeGroup.getId());
                        elemeItem.setElemeItemId(BigInteger.valueOf(elemeItemJsonObject.getLong("id")));
                        elemeItem.setSkuId(BigInteger.valueOf(elemeItemJsonObject.getLong("skuId")));
                        elemeItem.setCategoryId(BigInteger.valueOf(elemeItemJsonObject.getLong("categoryId")));
                        elemeItem.setPrice(BigDecimal.valueOf(elemeItemJsonObject.optDouble("price")));
                        elemeItem.setQuantity(elemeItemJsonObject.optInt("quantity"));
                        elemeItem.setTotal(BigDecimal.valueOf(elemeItemJsonObject.optDouble("total")));
                        elemeItem.setExtendCode(elemeItemJsonObject.optString("extendCode"));
                        elemeItem.setBarCode(elemeItemJsonObject.optString("barCode"));

                        Object weight = elemeItemJsonObject.opt("weight");
                        if (weight != null) {
                            elemeItem.setWeight(BigDecimal.valueOf(Double.valueOf(weight.toString())));
                        }
                        elemeItem.setCreateUserId(userId);
                        elemeItem.setLastUpdateUserId(userId);
                        elemeItem.setLastUpdateRemark("饿了么系统推送新订单，保存菜品属性！");
                        elemeItemMapper.insert(elemeItem);

                        JSONArray elemeItemAttributeJsonArray = elemeItemJsonObject.optJSONArray("attributes");
                        if (elemeItemAttributeJsonArray != null) {
                            int elemeItemAttributeJsonArraySize = elemeItemAttributeJsonArray.size();
                            for (int elemeItemAttributeJsonArrayIndex = 0; elemeItemAttributeJsonArrayIndex < elemeItemAttributeJsonArraySize; elemeItemAttributeJsonArrayIndex++) {
                                JSONObject elemeItemAttributeJsonObject = elemeItemAttributeJsonArray.optJSONObject(index);
                                ElemeItemAttribute elemeItemAttribute = new ElemeItemAttribute();
                                elemeItemAttribute.setElemeItemId(elemeItem.getId());
                                elemeItemAttribute.setName(elemeItemAttributeJsonObject.optString("name"));
                                elemeItemAttribute.setValue(elemeItemAttributeJsonObject.optString("value"));
                                elemeItemAttributeMapper.insert(elemeItemAttribute);
                            }
                        }

                        JSONArray elemeItemNewSpecJsonArray = elemeItemJsonObject.optJSONArray("newSpecs");
                        if (elemeItemNewSpecJsonArray != null) {
                            int elemeItemNewSpecJsonArraySize = elemeItemNewSpecJsonArray.size();
                            for (int elemeItemNewSpecJsonArrayIndex = 0; elemeItemNewSpecJsonArrayIndex < elemeItemNewSpecJsonArraySize; elemeItemNewSpecJsonArrayIndex++) {
                                JSONObject elemeItemNewSpecJsonObject = elemeItemNewSpecJsonArray.optJSONObject(index);
                                ElemeItemNewSpec elemeItemNewSpec = new ElemeItemNewSpec();
                                elemeItemNewSpec.setElemeItemId(elemeItem.getId());
                                elemeItemNewSpec.setName(elemeItemNewSpecJsonObject.optString("name"));
                                elemeItemNewSpec.setValue(elemeItemNewSpecJsonObject.optString("value"));
                                elemeItemNewSpec.setCreateUserId(userId);
                                elemeItemNewSpec.setLastUpdateUserId(userId);
                                elemeItemNewSpec.setLastUpdateRemark("饿了么系统推送新订单，保存菜品规格！");
                                elemeItemNewSpecMapper.insert(elemeItemNewSpec);
                            }
                        }
                    }
                }

                if (orderActivityJsonArray != null) {
                    int orderActivityJsonArraySize = orderActivityJsonArray.size();
                    for (int orderActivityJsonArrayIndex = 0; orderActivityJsonArrayIndex < orderActivityJsonArraySize; orderActivityJsonArrayIndex++) {
                        JSONObject elemeActivityJsonObject = orderActivityJsonArray.optJSONObject(orderActivityJsonArrayIndex);
                        ElemeActivity elemeActivity = new ElemeActivity();
                        elemeActivity.setElemeOrderId(elemeOrder.getId());
                        elemeActivity.setOrderId(orderId);
                        elemeActivity.setElemeActivityId(BigInteger.valueOf(elemeActivityJsonObject.optLong("id")));
                        elemeActivity.setName(elemeActivityJsonObject.optString("name"));
                        elemeActivity.setCategoryId(elemeActivityJsonObject.optInt("categoryId"));
                        elemeActivity.setMeaning(elemeActivityCategoryIdMeaningMap.get(elemeActivity.getCategoryId()));
                        elemeActivity.setAmount(BigDecimal.valueOf(elemeActivityJsonObject.optDouble("amount")));
                        elemeActivity.setCreateUserId(userId);
                        elemeActivity.setLastUpdateUserId(userId);
                        elemeActivity.setLastUpdateRemark("饿了么系统推送新订单，保存饿了么订单活动！");
                        elemeActivityMapper.insert(elemeActivity);
                    }
                }

                /*SearchModel branchSearchModel = new SearchModel(true);
                branchSearchModel.addSearchCondition("shopId", Constants.SQL_OPERATION_SYMBOL_EQUALS, shopId);
                Branch branch = branchMapper.find(branchSearchModel);
                Validate.notNull(branch, "shopId为" + shopId + "的门店不存在！");
                // 开始保存饿了么订单




                String elemeOrderMessageChannel = ConfigurationUtils.getConfiguration(Constants.ELEME_ORDER_MESSAGE_CHANNEL);
                JSONObject messageJsonObject = new JSONObject();
                messageJsonObject.put("tenantIdAndBranchId", branch.getTenantId() + "_" + branch.getId());
                messageJsonObject.put("type", type);
                messageJsonObject.put("orderId", 100);
                QueueUtils.publish(elemeOrderMessageChannel, messageJsonObject.toString());*/

                apiRest = new ApiRest();
                apiRest.setMessage("保存订单成功！");
                apiRest.setSuccessful(true);
            }
        } catch (Exception e) {
            CacheUtils.del(key);
            throw e;
        }
        return apiRest;
    }

    /**
     * 处理饿了么退单消息
     * @param shopId：店铺ID
     * @param message：消息内容
     * @param type：消息类型
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ApiRest handleElemeRefundOrderMessage(BigInteger shopId, JSONObject message, Integer type) throws IOException {
        ApiRest apiRest = null;
        String orderId = message.optString("id");
        String key = "_eleme_order_callback_" + orderId + "_" + type;
        try {
            Long returnValue = CacheUtils.setnx(key, key);
            if (returnValue == 0) {
                apiRest = new ApiRest();
                apiRest.setMessage("处理退单消息成功！");
                apiRest.setSuccessful(true);
            } else {
                SearchModel elemeOrderSearchModel = new SearchModel(true);
                elemeOrderSearchModel.addSearchCondition("eleme_order_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, orderId);
                ElemeOrder elemeOrder = elemeOrderMapper.find(elemeOrderSearchModel);
                Validate.notNull(elemeOrder, "饿了么订单不存在！");

                String refundStatus = message.optString("refundStatus");
                elemeOrder.setRefundStatus(refundStatus);

                BigInteger userId = CommonUtils.getServiceSystemUserId();
                elemeOrder.setLastUpdateUserId(userId);
                elemeOrder.setLastUpdateRemark("处理饿了么系统退单消息回调！");
                elemeOrderMapper.update(elemeOrder);

                SearchModel dietOrderSearchModel = new SearchModel(true);
                dietOrderSearchModel.addSearchCondition("order_number", Constants.SQL_OPERATION_SYMBOL_EQUALS, "E" + orderId);
                DietOrder dietOrder = dietOrderMapper.find(dietOrderSearchModel);
                Validate.notNull(dietOrder, "订单不存在！");

                int dietOrderRefundStatus = 0;
                if (ElemeOrderConstants.REFUND_STATUS_NO_REFUND.equals(refundStatus)) {
                    dietOrderRefundStatus = 1;
                } else if (ElemeOrderConstants.REFUND_STATUS_APPLIED.equals(refundStatus)) {
                    dietOrderRefundStatus = 2;
                } else if (ElemeOrderConstants.REFUND_STATUS_REJECTED.equals(refundStatus)) {
                    dietOrderRefundStatus = 3;
                } else if (ElemeOrderConstants.REFUND_STATUS_ARBITRATING.equals(refundStatus)) {
                    dietOrderRefundStatus = 2;
                } else if (ElemeOrderConstants.REFUND_STATUS_FAILED.equals(refundStatus)) {
                    dietOrderRefundStatus = 4;
                } else if (ElemeOrderConstants.REFUND_STATUS_SUCCESSFUL.equals(refundStatus)) {
                    dietOrderRefundStatus = 5;
                }
                dietOrder.setRefundStatus(dietOrderRefundStatus);
                dietOrder.setLastUpdateUserId(userId);
                dietOrder.setLastUpdateRemark("处理饿了么系统退单消息回调！");
                dietOrderMapper.update(dietOrder);

                ElemeRefundOrderMessage elemeRefundOrderMessage = new ElemeRefundOrderMessage();
                elemeRefundOrderMessage.setElemeOrderId(elemeOrder.getId());
                elemeRefundOrderMessage.setOrderId(elemeOrder.getOrderId());
                elemeRefundOrderMessage.setRefundStatus(message.optString("refundStatus"));
                elemeRefundOrderMessage.setReason(message.optString("reason"));
                elemeRefundOrderMessage.setShopId(shopId);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(message.getLong("updateTime") * 1000);
                elemeRefundOrderMessage.setUpdateTime(calendar.getTime());
                elemeRefundOrderMessage.setTenantId(dietOrder.getTenantId());
                elemeRefundOrderMessage.setBranchId(dietOrder.getBranchId());
                elemeRefundOrderMessage.setCreateUserId(userId);
                elemeRefundOrderMessage.setLastUpdateUserId(userId);
                elemeRefundOrderMessage.setLastUpdateRemark("饿了么系统回调，保存饿了么退单信息！");
                elemeRefundOrderMessageMapper.insert(elemeRefundOrderMessage);

                apiRest = new ApiRest();
                apiRest.setMessage("处理饿了么系统退单回调成功！");
                apiRest.setSuccessful(true);
            }
        } catch (Exception e) {
            CacheUtils.del(key);
            throw e;
        }
        return apiRest;
    }

    private static Map<Integer, String> elemeActivityCategoryIdMeaningMap = null;
    static {
        elemeActivityCategoryIdMeaningMap = new HashMap<Integer, String>();
        elemeActivityCategoryIdMeaningMap.put(11, "食物活动");
        elemeActivityCategoryIdMeaningMap.put(12, "餐厅活动");
        elemeActivityCategoryIdMeaningMap.put(15, "商家代金券抵扣");
        elemeActivityCategoryIdMeaningMap.put(200, "限时抢购");
    }
}