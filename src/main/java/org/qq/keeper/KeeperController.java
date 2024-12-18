package org.qq.keeper;

import org.qq.keeper.dto.CancelDTO;
import org.qq.keeper.dto.Result;
import org.qq.keeper.dto.StopDTO;
import org.qq.keeper.stop.StopApp;
import org.qq.keeper.stop.Win10Stop;
import org.qq.keeper.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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

        // 判断是否需要关机
        if(dto.isTimeToStop() && StrUtil.isNotEmpty(dto.getShutdownType())){
            StopApp stopApp = new Win10Stop();
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
        Timer timer = taskMap.get(dto.getAppName());
        timer.cancel();
        return new Result<>().success("cancel now");
    }


    private void stopApp(String appName){
        log.info("=== Stop app {}",appName);
        StopApp stopApp = new Win10Stop();
        stopApp.stop(appName);
    }

}
