package org.qq.keeper;

import org.qq.keeper.dto.CancelDTO;
import org.qq.keeper.dto.Result;
import org.qq.keeper.dto.StopDTO;
import org.qq.keeper.dto.TaskDTO;
import org.qq.keeper.stop.StopApp;
import org.qq.keeper.stop.Win10Stop;
import org.qq.keeper.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("api/keeper")
public class KeeperController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KeeperController.class);

    private Map<String, Timer> taskMap = new ConcurrentHashMap<>();

    @Autowired
    private KeeperConfig keeperConfig;

    @PostMapping("/stop")
    public Result stop(StopDTO dto){
        log.info("=== stop app {}", dto);
        StopApp stopApp = new Win10Stop();

        // 判断是否需要关机
        if(dto.isTimeToStop() && StrUtil.isNotEmpty(dto.getShutdownType())){
            cancelTask(dto.getAppName());
            stopApp.shutdown(dto.getShutdownType());
            return new Result<>().success("shutdown now");
        }
        if (dto.isTimeToStop()){
            stopApp(dto.getAppName());
            return new Result<>().success("stop time is now");
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (StrUtil.isNotEmpty(dto.getShutdownType())){
                    cancelTask(dto.getAppName());
                    stopApp.shutdown(dto.getShutdownType());
                }
                stopApp(dto.getAppName());
            }
        };
        long delay = dto.getHours() * 60 * 60 * 1000L + dto.getMinutes() * 60 * 1000L;
        Timer timer =  new Timer();
        timer.schedule(task, delay);
        taskMap.put(dto.getAppName(), timer);
        log.info("=== delay stop app {} in {} h {} m", dto.getAppName(), dto.getHours(), dto.getMinutes());
        return new Result<>().success( "stop time is delay in  " + dto.getHours() + "hours " + dto.getMinutes() + "minutes");
    }

    @PostMapping("/cancel")
    public Result cancel(CancelDTO dto){
        cancelTask(dto.getAppName());
        return new Result<>().success("cancel now");
    }
    private void cancelTask(String appName){
        Timer timer = taskMap.get(appName);
        if (timer == null){
            log.info("=== cancel stop app {} not found",appName);
            return;
        }
        timer.cancel();
        timer.purge();
        taskMap.remove(appName);
        log.info("=== cancel stop app {}",appName);
    }
    @GetMapping("/task-list")
    public Result taskList(){
        List<TaskDTO> taskDTOList = new ArrayList<>();
        taskMap.forEach((k,v) -> {
            log.info("{} {}", k, v);
            taskDTOList.add(new TaskDTO().setTaskId(k)
                    .setStopDTO(new StopDTO().setAppName(k))
            );


        });


        return new Result<>().success(taskDTOList);
    }



    private void stopApp(String appName){
        log.info("=== Stop app {}",appName);
        StopApp stopApp = new Win10Stop();
        stopApp.stop(appName);
    }

}
