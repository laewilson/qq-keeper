$(document).ready(function () {
    keeper.init();
});
keeper = {
    init: function () {
        this.bindEvents();
    },
    expiredTime: 0,
    bindEvents: function () {
        //开始按钮点击事件
        $('#start').click(function () {
            var totalSeconds = keeper.getTimeoutSecs();
            keeper.expiredTime = Date.now() + totalSeconds * 1000;
            keeper.action(function () {
                keeper.startBtn.disable();
                keeper.play = true;
                keeper.startCountdown(totalSeconds);
            });
        });
        //停止按钮点击事件
        $('#cancel').click(function () {
            keeper.cancel();
            keeper.expiredTime = 0;
        });
        //播放器选择事件
        $("#player-selection").change(function() {
            const selectElement = document.getElementById("player-selection");
            const selectedOption = selectElement.options[selectElement.selectedIndex];
            //选中的样式跟随选项
            let className = selectedOption.getAttribute("class");
            selectElement.setAttribute("class", "setting-select "+className);
        });
        //休息时间选择事件
        $("#restCheck").change(function() {
            let enableRest = $(this).is(":checked");
            if (enableRest) {
                $(".rest-item").show();
            }else {
                $(".rest-item").hide();
            }
        });
        document.addEventListener('visibilitychange', keeper.handleVisibilityChange, false);
    },

    startBtn:{
      enable: function () {
          $('.start-btn').attr('disabled', false);
          $('.start-btn').removeClass('disabled');

      },
      disable: function () {
          $('.start-btn').attr('disabled', true);
          $('.start-btn').addClass('disabled');

      }
    },
    handleVisibilityChange:function () {

        if (keeper.expiredTime <=0){
            return
        }
        if (keeper.expiredTime < Date.now()) {
            return;
        }

        if (document.visibilityState === 'visible') {
            // 页面重新显示时的处理逻辑
            console.log('Page is now visible,  resume counting down');
            // 立即执行一次任务
            keeper.setInterval();
        } else {
            console.log('Page is now hidden, pause counting down');
            // 如果需要可以在页面隐藏时清除定时器以节省资源
            clearInterval(keeper.interval);
        }
    },
    getTimeoutSecs: function () {
        let restCheck = $("#restCheck").is(":checked");
        var hours, minutes;
        if (restCheck){
             hours = parseInt(this.play ? $('#hours').val() : $('#restHours').val());
             minutes = parseInt(this.play ? $('#minutes').val() :  $('#restMinutes').val());
        }else {
             hours = parseInt($('#hours').val());
             minutes = parseInt($('#minutes').val());
        }
        var totalSeconds = keeper.count(hours, minutes);
        return totalSeconds;
    },
    timeoutAction: function () {



    },
    enableRest: function () {
        $(".set-btn").attr('disabled', true);
    },
    disableRest: function () {
        $(".set-btn").attr('disabled', false);
    },

    action: function (actionSuccess) {
        console.log('keeper action '+ new Date());
        let shutdownType = $("#shutdownCheck").is(":checked")? $("#shutdown-selection").val() : null;
        let hours = parseInt($('#hours').val());
        let minutes = parseInt($('#minutes').val());
        $.post('/api/keeper/stop', {
            appName: $("#player-selection").val(),
            appId: $("#player-selection").find(":selected").data("id")
            ,hours: hours
            ,minutes: minutes
            ,shutdownType: shutdownType
        }, function(data) {
            console.log(data);
            keeper.play = false;
            if (isFunction(actionSuccess)) {
                actionSuccess();
            }

        }).fail(function(xhr, status, error) {
            alert("【设置定时任务】发生错误: 请检查后台程序");
        });
    },
    restAction: function () {
        let enableRest = $("#restCheck").is(":checked");
        //
        if (enableRest) {
            keeper.enableRest();
            var totalSeconds = keeper.getTimeoutSecs();
            keeper.expiredTime = Date.now() + totalSeconds * 1000;
            keeper.startCountdown(totalSeconds);
        }
    },
    interval: null,
    cancel: function () {
        console.log('keeper cancel '+ new Date());
        //后台cancel
        $.post('/api/keeper/cancel', {
            appName: $("#player-selection").val()
        }, function(data) {
            console.log(data);
            keeper.reset();

        }).fail(function(xhr, status, error) {
            alert("【取消任务】发生错误: 请检查后台程序");
        });

    },
    count: function (hours, minutes) {
        return hours*60*60 + minutes*60;
    },
    reset: function () {
        this.resetCountdown();
        keeper.startBtn.enable();
    },
    resetCountdown: function () {
        document.getElementById('countdown').innerHTML = "00:00:00";
    },
    play: true,
    startCountdown: function(totalSeconds) {
        if(keeper.play) {
            $(".timeout-title").html("播放倒计时");
        }else {
            $(".timeout-title").html("休息倒计时");
        }
        this.resetCountdown();
        keeper.setInterval();
    },
    setInterval: function () {
        keeper.interval = setInterval(function() {
            keeper.countDown();
        }, 1000); // 每秒更新一次
    },
    stop: function () {
        clearInterval(this.interval);
    },
    countDown: function () {
        var now = new Date().getTime(); // 当前时间
        // 计算剩余时间
        var remainingTime = keeper.expiredTime - now;

        if (remainingTime <= 0) {
            keeper.stop();
            if (!keeper.play) {
                keeper.disableRest();
                keeper.play = true;
                console.log('rest');
                keeper.startBtn.enable();
                return;
            }
            document.getElementById('countdown').innerHTML = "时间到！";
            // 执行倒计时结束后的操作
            keeper.action(function () {
                keeper.restAction();
            });
            return;
        }
        var remainingSeconds = Math.ceil(remainingTime / 1000);
        var hours = Math.floor(remainingSeconds / 3600);
        var minutes = Math.floor((remainingSeconds % 3600) / 60);
        var seconds = Math.ceil(remainingSeconds % 60);

        // 格式化输出
        hours = (hours < 10) ? "0" + hours : hours;
        minutes = (minutes < 10) ? "0" + minutes : minutes;
        seconds = (seconds < 10) ? "0" + seconds : seconds;
        // 显示
        let countdown = hours + ":" + minutes + ":" + seconds;
        //            console.log("current time: "+countdown);
        document.getElementById('countdown').innerHTML = countdown;
    },

}

function isFunction(value) {
    return typeof value == 'function';
}