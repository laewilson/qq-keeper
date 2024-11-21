package org.qq.keeper;

import org.qq.keeper.dto.Result;
import org.qq.keeper.dto.StopDTO;
import org.qq.keeper.stop.StopApp;
import org.qq.keeper.stop.Win10Stop;
import org.qq.keeper.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.TimerTask;

@RestController
@RequestMapping("api/keeper")
public class KeeperController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KeeperController.class);

    @Autowired
    private KeeperConfig keeperConfig;

    @PostMapping("/stop")
    public Result stop(StopDTO dto){

        if(StrUtil.isNotEmpty(dto.getShutdownType())){
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
        new java.util.Timer().schedule(task, delay);


        return new Result<>().success( "stop time is delay in  " + dto.getHours() + "hours " + dto.getMinutes() + "minutes");
    }
    private void stopApp(String appName){
        log.info("=== Stop app {}",appName);
        StopApp stopApp = new Win10Stop();
        stopApp.stop(appName);
    }

}
