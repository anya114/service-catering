package build.dream.catering.controllers;

import build.dream.catering.models.branch.*;
import build.dream.catering.services.BranchService;
import build.dream.common.api.ApiRest;
import build.dream.common.controllers.BasicController;
import build.dream.common.utils.ApplicationHandler;
import build.dream.common.utils.GsonUtils;
import build.dream.common.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping(value = "/branch")
public class BranchController extends BasicController {
    @Autowired
    private BranchService branchService;

    @RequestMapping(value = "/initializeBranch")
    @ResponseBody
    public String initializeBranch() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            InitializeBranchModel initializeBranchModel = ApplicationHandler.instantiateObject(InitializeBranchModel.class, requestParameters);
            initializeBranchModel.validateAndThrow();
            apiRest = branchService.initializeBranch(initializeBranchModel);
        } catch (Exception e) {
            LogUtils.error("初始化门店失败", controllerSimpleName, "initializeBranch", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    @RequestMapping(value = "/listBranches")
    @ResponseBody
    public String listBranches() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ListBranchesModel listBranchesModel = ApplicationHandler.instantiateObject(ListBranchesModel.class, requestParameters);
            listBranchesModel.validateAndThrow();
            apiRest = branchService.listBranches(listBranchesModel);
        } catch (Exception e) {
            LogUtils.error("查询门店列表失败", controllerSimpleName, "listBranches", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 删除门店
     *
     * @return
     */
    @RequestMapping(value = "/deleteBranch")
    @ResponseBody
    public String deleteBranch() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DeleteBranchModel deleteBranchModel = ApplicationHandler.instantiateObject(DeleteBranchModel.class, requestParameters);
            deleteBranchModel.validateAndThrow();
            apiRest = branchService.deleteBranch(deleteBranchModel);
        } catch (Exception e) {
            LogUtils.error("删除门店信息失败", controllerSimpleName, "deleteBranch", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 拉取门店信息
     *
     * @return
     */
    @RequestMapping(value = "/pullBranchInfos")
    @ResponseBody
    public String pullBranchInfos() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            PullBranchInfosModel pullBranchInfosModel = ApplicationHandler.instantiateObject(PullBranchInfosModel.class, requestParameters);
            pullBranchInfosModel.validateAndThrow();
            apiRest = branchService.pullBranchInfos(pullBranchInfosModel);
        } catch (Exception e) {
            LogUtils.error("拉取门店信息失败", controllerSimpleName, "pullBranchInfos", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 禁用门店产品
     *
     * @return
     */
    @RequestMapping(value = "/disableGoods")
    @ResponseBody
    public String disableGoods() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            DisableGoodsModel disableGoodsModel = ApplicationHandler.instantiateObject(DisableGoodsModel.class, requestParameters);
            disableGoodsModel.validateAndThrow();
            apiRest = branchService.disableGoods(disableGoodsModel);
        } catch (Exception e) {
            LogUtils.error("禁用门店产品失败", controllerSimpleName, "disableGoods", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 门店续费回调
     *
     * @return
     */
    @RequestMapping(value = "/renewCallback")
    @ResponseBody
    public String renewCallback() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            RenewCallbackModel renewCallbackModel = ApplicationHandler.instantiateObject(RenewCallbackModel.class, requestParameters);
            renewCallbackModel.validateAndThrow();
            apiRest = branchService.handleRenewCallback(renewCallbackModel);
        } catch (Exception e) {
            LogUtils.error("处理门店续费回调失败", controllerSimpleName, "renewCallback", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }

    /**
     * 获取门店信息
     * @return
     */
    @RequestMapping(value = "/obtainBranchInfo")
    @ResponseBody
    public String obtainBranchInfo() {
        ApiRest apiRest = null;
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        try {
            ObtainBranchInfoModel obtainBranchInfoModel = ApplicationHandler.instantiateObject(ObtainBranchInfoModel.class, requestParameters);
            obtainBranchInfoModel.validateAndThrow();
            apiRest = branchService.obtainBranchInfo(obtainBranchInfoModel);
        } catch (Exception e) {
            LogUtils.error("获取门店信息失败", controllerSimpleName, "obtainBranchInfo", e, requestParameters);
            apiRest = new ApiRest(e);
        }
        return GsonUtils.toJson(apiRest);
    }
}
