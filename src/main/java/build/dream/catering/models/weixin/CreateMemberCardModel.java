package build.dream.catering.models.weixin;

import build.dream.common.models.CateringBasicModel;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class CreateMemberCardModel extends CateringBasicModel {
    @NotNull
    @Length(max = 12)
    private String brandName;

    @NotNull
    @Length(max = 9)
    private String title;

    @NotNull
    private String color;

    @NotNull
    @Length(max = 16)
    private String notice;

    @Length(max = 24)
    private String servicePhone;

    @NotNull
    @Length(max = 1024)
    private String description;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getServicePhone() {
        return servicePhone;
    }

    public void setServicePhone(String servicePhone) {
        this.servicePhone = servicePhone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
