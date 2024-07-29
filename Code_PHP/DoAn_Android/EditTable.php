<?php
header('Content-Type: application/json; charset=utf-8');

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "ql_cafe";

// Kết nối đến cơ sở dữ liệu
$conn = new mysqli($servername, $username, $password, $dbname);

// Kiểm tra kết nối
if ($conn->connect_error) {
    die(json_encode(array("status" => "error", "message" => "Kết nối thất bại: " . $conn->connect_error), JSON_UNESCAPED_UNICODE));
}

// Lấy dữ liệu từ yêu cầu bằng $_POST
$maBan = $_POST["maBan"];
$tableName = $_POST["tableName"];

// Kiểm tra đầu vào
if (empty($maBan) || empty($tableName)) {
    $response = array(
        "status" => "error",
        "message" => "Vui lòng điền đầy đủ thông tin!"
    );
    echo json_encode($response);
    exit();
}

try {
    // Cập nhật thông tin bàn trong bảng "ban"
    $sql = "UPDATE ban SET TenBan = ? WHERE MaBan = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $tableName, $maBan);

    if ($stmt->execute()) {
        if ($stmt->affected_rows > 0) {
            $response = array("status" => "success", "message" => "Cập nhật bàn thành công!");
        } else {
            $response = array("status" => "error", "message" => "Không tìm thấy bàn với mã bàn này!");
        }
        echo json_encode($response);
    } else {
        $response = array("status" => "error", "message" => "Cập nhật bàn thất bại! Lỗi: " . $stmt->error);
        echo json_encode($response);
    }
} catch (Exception $e) {
    $response = array("status" => "error", "message" => "Lỗi hệ thống: " . $e->getMessage());
    echo json_encode($response);
} finally {
    $stmt->close();
    $conn->close();
}
?>
