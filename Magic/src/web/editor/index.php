<?php
require_once('../config.inc.php');
require_once('user.inc.php');
if (!$sandboxServer) die('No sandbox server defined');

$user = getUser();

?>

<html>
<head>
    <title><?= $title ?> Editor</title>
    <link rel="shortcut icon" type="image/x-icon" href="favicon.ico">
    <link rel="stylesheet" href="common/css/smoothness/jquery-ui-1.10.3.custom.min.css"/>
    <link rel="stylesheet" href="common/css/common.css" />
    <link rel="stylesheet" href="common/css/loading.css" />
    <link rel="stylesheet" href="css/editor.css"/>
    <link rel="stylesheet" href="css/user.css"/>
    <script src="common/js/jquery-1.10.2.min.js"></script>
    <script src="common/js/jquery-ui-1.10.3.custom.min.js"></script>
    <script src="js/editor.js"></script>
    <script src="js/user.js"></script>
    <script type="text/javascript">
        var user = <?= json_encode($user) ?>;
    </script>
    <?php if ($analytics) echo $analytics; ?>
</head>
<body>
<div id="container">
<div id="header">
    <span>
        <button type="button" id="saveButton">Save</button>
    </span>
    <span id="userInfo">
        <div>
            <span id="userSkin">&nbsp;</span>
            <span id="userOverlay">&nbsp;</span>
        </div>
        <div>
            <span id="userName"></span><br/>
            <span id="loginButton" style="display: none">
                Log in to Save
            </span>
            <span id="logoutButton" style="display: none">
                Log out
            </span>
        </div>
    </span>
</div>
<textarea id="editor">
<?php echo file_get_contents("$sandboxServer/plugins/Magic/spells.yml") ?>
</textarea>
</div>

<div id="registrationDialog" title="Log In" style="display: none">
    <div id="registrationTitle">Please log into the <span class="server"><?= $sandboxServerURL ?></span>s server and register</div>
    <div>
        <label for="userId">In-Game Name:</label><input type="text" id="userId">
    </div>
</div>


<div id="codeDialog" title="Enter Code" style="display:none">
  <div style="margin-bottom: 0.5em">
    <span class="ui-icon ui-icon-circle-check" style="float:left; margin:0 7px 50px 0;"></span>
    <span>Please enter the following code in-game using</span>
  </div>
  <div class="code">
    /magic register <span id="codeDiv"></span>
  </div>
</div>
</body>
</html>