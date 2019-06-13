<?php 

    error_reporting(E_ALL); 
    ini_set('display_errors',1); 

    include('dbcon.php');
    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android)
    {

        $num=$_POST['num'];
        $mac=$_POST['mac'];
        $start_login=$_POST['start_login'];

        if(empty($num)){
            $errMSG = "Please enter your name";
        }
        else if(empty($start_login)){
            $errMSG = "Please enter your start_login";
        }

        if(!isset($errMSG))
        {
            try{
                $stmt = $con->prepare('INSERT INTO login_log VALUES(:num, :mac,  :start_login)');
                $stmt->bindParam(':num', $num);
                $stmt->bindParam(':mac', $mac);
                $stmt->bindParam(':start_login', $start_login);

                if($stmt->execute())
                {
                    $successMSG = "Added new user";
                }
                else
				{
                    $errMSG = "User Added Error";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage()); 
            }
        }

    }
?>

<?php
if (isset($errMSG)) echo $errMSG;
if (isset($successMSG)) echo $successMSG;

$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

if(!$android)
{
?>
<html>
<body>

<form action="<?php $_PHP_SELF ?>" method="POST">
Num: <input type = "text" name = "num" />
Mac: <input type = "text" name = "mac" />
Start_login: <input type = "text" name = "start_login" />
<input type = "submit" name = "submit" />
</form>
</body>
</html>
<?php
}
?>



