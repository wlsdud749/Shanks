let emailCheck = true;
let passwordCheck = true;
let phoneNumberCheck = true;
$("#id").keyup(function () {
    $.ajax({
        type: "post",
        url: "/sign/id_check",
        data: { id: $("#id").val() },
        dataType: "json",
        success: function (res) {
            if (res.message === "빈 값입니다.") {
                $("#id").attr("class", "form-control mb-2");
                $("#id_fail").hide();
                $("#id_success").hide();
            } else if (res.message === "사용 가능한 아이디입니다.") {
                $("#id").attr("class", "form-control mb-2 is-valid");
                $("#id_success").show();
                $("#id_fail").hide();
            } else {
                $("#id").attr("class", "form-control mb-2 is-invalid");
                $("#id_success").hide();
                $("#id_fail").show();
            }
        },
        error: function (request, status, error) {
            console.log("code: " + request.status);
            console.log("message: " + request.responseText);
            console.log("error: " + error);
        },
    });
});

$("#password").keyup(function () {
    var pwd = $(this).val();
    var rePwd = $("#repeatpassword").val();
    // 비밀번호 같은지 확인
    if (rePwd == "") {
        $("#repeatpassword").attr("class", "form-control mb-2");
        $("#password_fail").hide();
        $("#password_success").hide();
        passwordCheck = true;
    }
    else if (rePwd == pwd) {//비밀번호 같다면
        $("#repeatpassword").attr("class", "form-control is-valid");
        $("#password_success").show();
        $("#password_fail").hide();
        passwordCheck = false;
    }
    else if (rePwd != pwd) {//비밀번호 다르다면
        $("#repeatpassword").attr("class", "form-control is-invalid");
        $("#password_success").hide();
        $("#password_fail").show();
        passwordCheck = true;
    }
});

$("#repeatpassword").keyup(function () {
    var rePwd = $(this).val();
    var pwd = $("#password").val();
    // 비밀번호 같은지 확인
    if (rePwd == "") {
        $("#repeatpassword").attr("class", "form-control mb-2");
        $("#password_fail").hide();
        $("#password_success").hide();
        passwordCheck = true;
    }
    else if (rePwd == pwd) {//비밀번호 같다면
        $("#repeatpassword").attr("class", "form-control is-valid");
        $("#password_success").show();
        $("#password_fail").hide();
        passwordCheck = false;
    }
    else if (rePwd != pwd) {//비밀번호 다르다면
        $("#repeatpassword").attr("class", "form-control is-invalid");
        $("#password_success").hide();
        $("#password_fail").show();
        passwordCheck = true;
    }
});

$("#nick").keyup(function () {
    $.ajax({
        type: "post",
        url: "/sign/nick_check",
        data: { nick: $("#nick").val() },
        dataType: "json",
        success: function (res) {
            if (res.message === "빈 값입니다.") {
                $("#nick").attr("class", "form-control mb-2");
                $("#nick_fail").hide();
                $("#nick_success").hide();
            } else if (res.message === "사용 가능한 닉네임입니다.") {
                $("#nick").attr("class", "form-control mb-2 is-valid");
                $("#nick_success").show();
                $("#nick_fail").hide();
            } else {
                $("#nick").attr("class", "form-control mb-2 is-invalid");
                $("#nick_success").hide();
                $("#nick_fail").show();
            }
        },
        error: function (request, status, error) {
            console.log("code: " + request.status);
            console.log("message: " + request.responseText);
            console.log("error: " + error);
        },
    });
});

$("#email").keyup(function () {
    let e = $(this).val();
    let reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    // 이메일 검증할 정규 표현식
    if (e == "") {
        $("#email").attr("class", "form-control mb-2");
        $("#Email_fail").hide();
        emailCheck = true;
    }
    else if (reg.test(e)) {//정규표현식을 통과 한다면
        $("#email").attr("class", "form-control mb-2 is-valid");
        $("#Email_fail").hide();
        emailCheck = false;
    }
    else {//정규표현식을 통과하지 못하면
        $("#email").attr("class", "form-control mb-2 is-invalid");
        $("#Email_fail").show();
        emailCheck = true;
    }
});

$("#phoneNumber").keyup(function () {
    let n = $(this).val();
    let reg = /^01([0|1|6|7|8|9])?([0-9]{3,4})?([0-9]{4})$/;
    // 전화번호 검증할 정규 표현식
    if (n == "") {
        $("#phoneNumber").attr("class", "form-control mb-2");
        $("#number_fail").hide();
        phoneNumberCheck = true;
    }
    else if (reg.test(n)) {//정규표현식을 통과 한다면
        $("#phoneNumber").attr("class", "form-control mb-2 is-valid");
        $("#number_fail").hide();
        phoneNumberCheck = false;
    }
    else {//정규표현식을 통과하지 못하면
        $("#phoneNumber").attr("class", "form-control mb-2 is-invalid");
        $("#number_fail").show();
        phoneNumberCheck = true;
    }
});

$("#signupbutton").click(function () {
    sendAjaxRequest();
});

$("form").submit(function (event) {
    event.preventDefault();
    sendAjaxRequest();
});

function sendAjaxRequest() {
    if (passwordCheck) {
        alert("비밀번호 확인을 다시해주세요.");
        return;
    }
    if (emailCheck) {
        alert("올바른 이메일 형식이 아닙니다. 다시 입력해 주세요.");
        return;
    }
    if (phoneNumberCheck) {
        alert("올바르지 않은 전화번호 형식입니다. 다시 입력해주세요.");
        return;
    }
    var memberDto = {
        "id": $("#id").val(),
        "password": $("#password").val(),
        "name": $("#name").val(),
        "email": $("#email").val(),
        "nick": $("#nick").val(),
        "phoneNumber": $("#phoneNumber").val(),
        "memberAddressDto": {
            "address": $("#address").val(),
            "zonecode": $("#postcode").val(),
            "detailedaddress": $("#detailAddress").val()
        }
    };
    $.ajax({
        url: "/sign/register",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(memberDto),
        success: function (response) {
            console.log("요청성공");
            window.location.href = "/sign/sign-in";
            alert("회원가입이 완료되었습니다.");
            $('form :input').val('');
        },
        error: function (xhr, textStatus, errorThrown) {
            console.log("에러발생", errorThrown);
            alert(xhr.responseText);
        }
    });
}

function DaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.
            let Jaddr = JSON.stringify(data, ['address']);
            let Zaddr = JSON.stringify(data, ['zonecode']);

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if (data.userSelectedType === 'R') {
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("extraAddress").value = extraAddr;

            } else {
                document.getElementById("extraAddress").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
            document.getElementById('postcode').value = data.zonecode;
            document.getElementById("address").value = addr;
            // 커서를 상세주소 필드로 이동한다.
            document.getElementById("detailAddress").focus();
        }
    }).open();
}