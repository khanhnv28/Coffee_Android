<?php
    $servername = "localhost";
    $username = "root";
    $password = "";
    $dbname = "ql_cafe";
    
    $conn = new mysqli($servername, $username, $password, $dbname);

    if ($conn->connect_error) {
        die("Lỗi Database: " . $conn->connect_error);
    }

    $MaLoai = $_POST['MaLoai'];
    $TenMon = $_POST['TenMon'];
    $MaMon = $_POST['MaMon'];
    $GiaBan = $_POST['GiaBan'];
    $HinhAnh = $_POST['HinhAnh'];
    $SL=0;



    $checkMaMon = "SELECT MaMon FROM mon WHERE MaMon = ?";
    $stmt = $conn->prepare($checkMaMon);
    $stmt->bind_param("s", $MaMon);
    $stmt->execute();
    $stmt->store_result();
    
    if ($stmt->num_rows == 0){
        echo "exist";
    } else {
        $sql = "UPDATE mon SET MaLoai=?, TenMon = ?,HinhAnh=?,GiaBan=? WHERE MaMon= ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("sssss", $MaLoai,  $TenMon,$HinhAnh,$GiaBan,$MaMon);
        
        if ($stmt->execute()) {
            echo "success";
        } else {
            echo "error";
        }
    }

    $stmt->close();
    $conn->close();
?>