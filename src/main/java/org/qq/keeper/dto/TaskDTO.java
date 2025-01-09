package org.qq.keeper.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskDTO {
    private String taskId;
    private StopDTO stopDTO;



}
