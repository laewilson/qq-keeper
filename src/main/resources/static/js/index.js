$(document).ready(function () {
    keeper.init();
});
keeper = {
    init: function () {
        this.bindEvents();
    },
    bindEvents: function () {
        $('#start').click(function () {
            console.log('start');
            keeper.startBtn.disable();
            var totalSeconds = keeper.getTimeoutSecs();
            keeper.startCountdown(totalSeconds);
        });
        $('#stop').click(function () {
            keeper.stop();
        });
        $("#player-selection").change(function() {
            const selectElement = document.getElementById("player-selection");
            const selectedOption = selectElement.options[selectElement.selectedIndex];
            //选中的样式跟随选项
            let className = selectedOption.getAttribute("class");
            selectElement.setAttribute("class", className);
        });
        $("#restCheck").change(function() {
            let enableRest = $(this).is(":checked");
            if (enableRest) {
                $(".rest-item").show();
            }else {
                $(".rest-item").hide();
            }
        });

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
    action: function () {
        console.log('keeper action');
        let shutdownType = $("#shutdownCheck").is(":checked")? $("#shutdown-selection").val() : null;
        $.post('/api/keeper/stop', {
            appName: $("#player-selection").val(),
            appId: $("#player-selection").find(":selected").data("id")
            ,shutdownType: shutdownType
        }, function(data) {
            console.log(data);
            keeper.play = false;
            let enableRest = $("#restCheck").is(":checked");
            //
            if (enableRest) {
                keeper.enableRest();
                var totalSeconds = keeper.getTimeoutSecs();
                keeper.startCountdown(totalSeconds);
            }

        });


    },
    interval: null,
    stop: function () {
        clearInterval(this.interval);
        this.reset();
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
        var startTime = new Date().getTime(); // 获取开始时间
//        totalSeconds= totalSeconds+0.1;
        this.interval = setInterval(function() {
            var now = new Date().getTime(); // 当前时间
            var elapsed = now - startTime; // 已经过的时间
            // 计算剩余时间
            var remainingTime = totalSeconds * 1000 - elapsed;

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
                keeper.action();
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

        }, 1000); // 每秒更新一次
    }
}

