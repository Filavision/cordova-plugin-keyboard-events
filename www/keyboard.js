var exec = require('cordova/exec');
var channel = require('cordova/channel');

var Keyboard = function () {};

Keyboard.isVisible = false;

Keyboard.fireOnShow = function (height) {
    Keyboard.isVisible = true;
    cordova.fireWindowEvent('keyboardDidShow', { keyboardHeight: height });
};

Keyboard.fireOnHide = function () {
    Keyboard.isVisible = false;
    cordova.fireWindowEvent('keyboardDidHide');
};

channel.onCordovaReady.subscribe(function () {
    exec(function (msg) {
        var action = msg.charAt(0);
        if (action === 'S') {
            var height = parseInt(msg.substr(1));
            Keyboard.fireOnShow(height);
        } else if (action === 'H') {
            Keyboard.fireOnHide();
        }
    }, null, 'KeyboardEvents', 'init', []);
});

module.exports = Keyboard;
