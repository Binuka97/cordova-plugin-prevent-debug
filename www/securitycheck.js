var exec = require('cordova/exec');

var SecurityCheck = {
    check: function(success, error) {
        exec(success, error, "SecurityCheck", "check", []);
    }
};

module.exports = SecurityCheck;
