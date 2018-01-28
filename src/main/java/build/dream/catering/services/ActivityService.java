package build.dream.catering.services;

import build.dream.catering.constants.Constants;
import build.dream.catering.mappers.*;
import build.dream.catering.models.activity.SaveBuyGiveActivityModel;
import build.dream.catering.utils.CanNotDeleteReasonUtils;
import build.dream.common.api.ApiRest;
import build.dream.common.erp.catering.domains.*;
import build.dream.common.utils.CacheUtils;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.SearchModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ActivityService {
    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private UniversalMapper universalMapper;
    @Autowired
    private BuyGiveActivityMapper buyGiveActivityMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsSpecificationMapper goodsSpecificationMapper;
    @Autowired
    private CanNotDeleteReasonMapper canNotDeleteReasonMapper;

    public ApiRest test() {
        String findAllBuyGiveActivitiesSql = "SELECT " +
                "activity.tenant_id, " +
                "activity.branch_id, " +
                "activity.tenant_code, " +
                "activity.id AS activity_id, " +
                "activity.name AS activity_name, " +
                "activity.type AS activity_type, " +
                "activity.status AS activity_status, " +
                "activity.start_time, " +
                "activity.end_time, " +
                "buy_goods.id AS buy_goods_id, " +
                "buy_goods.name AS buy_goods_name, " +
                "buy_goods_specification.id AS buy_goods_specification_id, " +
                "buy_goods_specification.name AS buy_goods_specification_name, " +
                "buy_give_activity.buy_quantity, " +
                "give_goods.id AS give_goods_id, " +
                "give_goods.name AS give_goods_name, " +
                "give_goods_specification.id AS give_goods_specification_id, " +
                "give_goods_specification.name AS give_goods_specification_name, " +
                "buy_give_activity.give_quantity " +
                "FROM activity " +
                "LEFT JOIN buy_give_activity ON activity.id = buy_give_activity.activity_id AND buy_give_activity.deleted = 0 " +
                "LEFT JOIN goods AS buy_goods ON buy_goods.id = buy_give_activity.buy_goods_id " +
                "LEFT JOIN goods_specification AS buy_goods_specification ON buy_goods_specification.id = buy_give_activity.buy_goods_specification_id " +
                "LEFT JOIN goods AS give_goods ON give_goods.id = buy_give_activity.give_goods_id " +
                "LEFT JOIN goods_specification AS give_goods_specification ON give_goods_specification.id = buy_give_activity.give_goods_specification_id " +
                "WHERE activity.tenant_id = #{tenantId} " +
                "AND activity.branch_id = #{branchId} " +
                "AND activity.status = #{status} " +
                "AND activity.type = #{type} " +
                "AND activity.deleted = 0";
        Map<String, Object> findAllBuyGiveActivitiesParameters = new HashMap<String, Object>();
        findAllBuyGiveActivitiesParameters.put("sql", findAllBuyGiveActivitiesSql);
        findAllBuyGiveActivitiesParameters.put("tenantId", BigInteger.ONE);
        findAllBuyGiveActivitiesParameters.put("branchId", BigInteger.ONE);
        findAllBuyGiveActivitiesParameters.put("status", 2);
        findAllBuyGiveActivitiesParameters.put("type", 1);
        List<Map<String, Object>> allBuyGiveActivities = universalMapper.executeQuery(findAllBuyGiveActivitiesParameters);

        if (CollectionUtils.isNotEmpty(allBuyGiveActivities)) {
            for (Map<String, Object> buyGiveActivity : allBuyGiveActivities) {
                BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "tenantId"));
                BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "branchId"));
                BigInteger buyGoodsId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "buyGoodsId"));
                BigInteger buyGoodsSpecificationId = BigInteger.valueOf(MapUtils.getLongValue(buyGiveActivity, "buyGoodsSpecificationId"));
                CacheUtils.hset(Constants.KEY_BUY_GIVE_ACTIVITIES, tenantId + "_" + branchId + "_" + buyGoodsId + "_" + buyGoodsSpecificationId, GsonUtils.toJson(buyGiveActivity));
            }
        }

        String findAllFullReductionActivitiesSql = "SELECT " +
                "activity.tenant_id, " +
                "activity.tenant_code, " +
                "activity.branch_id, " +
                "activity.id AS activity_id, " +
                "activity.name AS activity_name, " +
                "activity.type AS activity_type, " +
                "activity.status AS activity_status, " +
                "activity.start_time, " +
                "activity.end_time, " +
                "full_reduction_activity.total_amount, " +
                "full_reduction_activity.discount_type, " +
                "full_reduction_activity.discount_rate, " +
                "full_reduction_activity.discount_amount " +
                "FROM activity " +
                "LEFT JOIN full_reduction_activity ON activity.id = full_reduction_activity.activity_id " +
                "WHERE activity.tenant_id = #{tenantId} " +
                "AND activity.branch_id = #{branchId} " +
                "AND activity.status = #{status} " +
                "AND activity.type = #{type} " +
                "AND activity.deleted = 0";
        Map<String, Object> findAllFullReductionActivitiesParameters = new HashMap<String, Object>();
        findAllFullReductionActivitiesParameters.put("sql", findAllFullReductionActivitiesSql);
        findAllFullReductionActivitiesParameters.put("tenantId", BigInteger.ONE);
        findAllFullReductionActivitiesParameters.put("branchId", BigInteger.ONE);
        findAllFullReductionActivitiesParameters.put("status", 2);
        findAllFullReductionActivitiesParameters.put("type", 2);
        List<Map<String, Object>> allFullReductionActivities = universalMapper.executeQuery(findAllFullReductionActivitiesParameters);
        if (CollectionUtils.isNotEmpty(allFullReductionActivities)) {
            for (Map<String, Object> fullReductionActivity : allFullReductionActivities) {
                BigInteger tenantId = BigInteger.valueOf(MapUtils.getLongValue(fullReductionActivity, "tenantId"));
                BigInteger branchId = BigInteger.valueOf(MapUtils.getLongValue(fullReductionActivity, "branchId"));
                CacheUtils.hset(Constants.KEY_FULL_REDUCTION_ACTIVITIES, tenantId + "_" + branchId, GsonUtils.toJson(fullReductionActivity));
            }
        }

        return new ApiRest(allBuyGiveActivities, "查询成功！");
    }

    @Transactional(rollbackFor = Exception.class)
    public ApiRest saveBuyGiveActivity(SaveBuyGiveActivityModel saveBuyGiveActivityModel) throws ParseException {
        BigInteger tenantId = saveBuyGiveActivityModel.getTenantId();
        String tenantCode = saveBuyGiveActivityModel.getTenantCode();
        BigInteger branchId = saveBuyGiveActivityModel.getBranchId();
        BigInteger userId = saveBuyGiveActivityModel.getUserId();
        Activity activity = new Activity();
        activity.setTenantId(tenantId);
        activity.setTenantCode(tenantCode);
        activity.setBranchId(branchId);
        activity.setName(saveBuyGiveActivityModel.getName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DEFAULT_DATE_PATTERN);
        activity.setType(1);

        Date startTime = simpleDateFormat.parse(saveBuyGiveActivityModel.getStartTime() + " 00:00:00");
        activity.setStartTime(startTime);

        Date endTime = simpleDateFormat.parse(saveBuyGiveActivityModel.getEndTime() + " 23:59:59");
        activity.setEndTime(endTime);

        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis >= startTime.getTime() && currentTimeMillis <= endTime.getTime()) {
            activity.setStatus(2);
        } else {
            activity.setStatus(1);
        }
        activity.setCreateUserId(userId);
        activity.setLastUpdateUserId(userId);
        activity.setLastUpdateRemark("保存活动信息！");
        activityMapper.insert(activity);

        List<SaveBuyGiveActivityModel.BuyGiveActivityInfo> buyGiveActivityInfos = saveBuyGiveActivityModel.getBuyGiveActivityInfos();

        List<BigInteger> goodsIds = new ArrayList<BigInteger>();
        List<BigInteger> goodsSpecificationIds = new ArrayList<BigInteger>();
        for (SaveBuyGiveActivityModel.BuyGiveActivityInfo buyGiveActivityInfo : buyGiveActivityInfos) {
            goodsIds.add(buyGiveActivityInfo.getBuyGoodsId());
            goodsSpecificationIds.add(buyGiveActivityInfo.getBuyGoodsSpecificationId());
            goodsIds.add(buyGiveActivityInfo.getGiveGoodsId());
            goodsSpecificationIds.add(buyGiveActivityInfo.getGiveGoodsSpecificationId());
        }

        // 查询出涉及的所有商品
        SearchModel goodsSearchModel = new SearchModel(true);
        goodsSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsIds);
        goodsSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<Goods> goodses = goodsMapper.findAll(goodsSearchModel);

        // 查询出涉及的所有商品规格
        SearchModel goodsSpecificationSearchModel = new SearchModel(true);
        goodsSpecificationSearchModel.addSearchCondition("id", Constants.SQL_OPERATION_SYMBOL_IN, goodsSpecificationIds);
        goodsSpecificationSearchModel.addSearchCondition("tenant_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, tenantId);
        goodsSpecificationSearchModel.addSearchCondition("branch_id", Constants.SQL_OPERATION_SYMBOL_EQUALS, branchId);
        List<GoodsSpecification> goodsSpecifications = goodsSpecificationMapper.findAll(goodsSpecificationSearchModel);

        // 封装商品id与商品之间的map
        Map<BigInteger, Goods> goodsMap = new HashMap<BigInteger, Goods>();
        for (Goods goods : goodses) {
            goodsMap.put(goods.getId(), goods);
        }

        // 封装商品规格id与商品规格之间的map
        Map<BigInteger, GoodsSpecification> goodsSpecificationMap = new HashMap<BigInteger, GoodsSpecification>();
        for (GoodsSpecification goodsSpecification : goodsSpecifications) {
            goodsSpecificationMap.put(goodsSpecification.getId(), goodsSpecification);
        }

        List<BuyGiveActivity> buyGiveActivities = new ArrayList<BuyGiveActivity>();
        List<CanNotDeleteReason> canNotDeleteReasons = new ArrayList<CanNotDeleteReason>();
        for (SaveBuyGiveActivityModel.BuyGiveActivityInfo buyGiveActivityInfo : buyGiveActivityInfos) {
            Goods buyGoods = goodsMap.get(buyGiveActivityInfo.getBuyGoodsId());
            Validate.notNull(buyGoods, "商品不存在！");

            GoodsSpecification buyGoodsSpecification = goodsSpecificationMap.get(buyGiveActivityInfo.getBuyGoodsSpecificationId());
            Validate.notNull(buyGoods, "商品规格不存在！");

            Goods giveGoods = goodsMap.get(buyGiveActivityInfo.getGiveGoodsId());
            Validate.notNull(buyGoods, "商品不存在！");

            GoodsSpecification giveGoodsSpecification = goodsSpecificationMap.get(buyGiveActivityInfo.getGiveGoodsSpecificationId());
            Validate.notNull(buyGoods, "商品规格不存在！");

            BuyGiveActivity buyGiveActivity = new BuyGiveActivity();
            buyGiveActivity.setTenantId(tenantId);
            buyGiveActivity.setTenantCode(tenantCode);
            buyGiveActivity.setBranchId(branchId);
            buyGiveActivity.setActivityId(activity.getId());
            buyGiveActivity.setBuyGoodsId(buyGoods.getId());
            buyGiveActivity.setBuyGoodsSpecificationId(buyGoodsSpecification.getId());
            buyGiveActivity.setBuyQuantity(buyGiveActivityInfo.getBuyQuantity());
            buyGiveActivity.setGiveGoodsId(giveGoods.getId());
            buyGiveActivity.setGiveGoodsSpecificationId(giveGoodsSpecification.getId());
            buyGiveActivity.setGiveQuantity(buyGiveActivityInfo.getGiveQuantity());
            buyGiveActivity.setCreateUserId(userId);
            buyGiveActivity.setLastUpdateUserId(userId);
            buyGiveActivity.setLastUpdateRemark("保存买A赠B活动！");
            buyGiveActivities.add(buyGiveActivity);

            String goodsReason = "该商品包含在促销活动【" + activity.getName() + "】中，不能删除！";
            String goodsSpecificationReason = "该商品规格包含在促销活动【" + activity.getName() + "】中，不能删除！";
            canNotDeleteReasons.add(CanNotDeleteReasonUtils.constructCanNotDeleteReason(tenantId, tenantCode, tenantId, buyGoods.getId(), "goods", activity.getId(), "activity", goodsReason));
            canNotDeleteReasons.add(CanNotDeleteReasonUtils.constructCanNotDeleteReason(tenantId, tenantCode, tenantId, buyGoodsSpecification.getId(), "goods", activity.getId(), "activity", goodsSpecificationReason));
            canNotDeleteReasons.add(CanNotDeleteReasonUtils.constructCanNotDeleteReason(tenantId, tenantCode, tenantId, giveGoods.getId(), "goods_specification", activity.getId(), "activity", goodsReason));
            canNotDeleteReasons.add(CanNotDeleteReasonUtils.constructCanNotDeleteReason(tenantId, tenantCode, tenantId, giveGoodsSpecification.getId(), "goods_specification", activity.getId(), "activity", goodsSpecificationReason));;
        }
        buyGiveActivityMapper.insertAll(buyGiveActivities);
        canNotDeleteReasonMapper.insertAll(canNotDeleteReasons);
        ApiRest apiRest = new ApiRest();
        apiRest.setMessage("保存买A赠B活动成功！");
        apiRest.setSuccessful(true);
        return apiRest;
    }
}
