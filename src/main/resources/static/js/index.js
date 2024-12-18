$(document).ready(function () {
    keeper.init();
});
keeper = {
    init: function () {
        this.bindEvents();
    },
    bindEvents: function () {
        //开始按钮点击事件
        $('#start').click(function () {
            var totalSeconds = keeper.getTimeoutSecs();
            keeper.action(function () {
                keeper.startBtn.disable();
                keeper.startCountdown(totalSeconds);
            });
        });
        //停止按钮点击事件
        $('#stop').click(function () {
            keeper.stop();
        });
        //
        $("#player-selection").change(function() {
            const selectElement = document.getElementById("player-selection");
            const selectedOption = selectElement.options[selectElement.selectedIndex];
            //选中的样式跟随选项
            let className = selectedOption.getAttribute("class");
            selectElement.setAttribute("class", "setting-select "+className);
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

    action: function (actionSuccess) {
        console.log('keeper action');
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
            alert("发生错误: 请检查后台程序");
        });
    },
    restAction: function () {
        let enableRest = $("#restCheck").is(":checked");
        //
        if (enableRest) {
            keeper.enableRest();
            var totalSeconds = keeper.getTimeoutSecs();
            keeper.startCountdown(totalSeconds);
        }
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

        }, 1000); // 每秒更新一次
    }
}

function isFunction(value) {
    return typeof value == 'function';
}