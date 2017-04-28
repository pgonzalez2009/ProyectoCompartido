
package es.cice.practicapedrogonzalez.json;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetalleSchema {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("result")
    @Expose
    private ResultDetalle resultDetalle;
    @SerializedName("status")
    @Expose
    private String status;

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public ResultDetalle getResultDetalle() {
        return resultDetalle;
    }

    public void setResultDetalle(ResultDetalle resultDetalle) {
        this.resultDetalle = resultDetalle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
