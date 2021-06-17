package jp.co.axa.apidemo.payload;

import lombok.Getter;
import lombok.Setter;
import io.swagger.annotations.ApiModelProperty;

public class MessageResponse {

    @Setter
    @Getter
    @ApiModelProperty(value = "message", example = "Success.")
    String message;

}
