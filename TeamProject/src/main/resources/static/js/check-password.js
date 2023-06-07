$("#checkPwBotton").click(function () {
    sendAjaxRequest();
});

$("form").submit(function (event) {
    event.preventDefault();
    sendAjaxRequest();
});

function sendAjaxRequest() {
    let password = $("#password").val().trim();
    if (password === '') {
        alert("비밀번호를 입력하세요.");
        return;
    }
    $.ajax({
        type: "post",
        url: "/profile/checkPw",
        data: { "password": password },
        success: function (response) {
            if (response === true) {
                window.location.href = "/profile/manage-profiles";
                $('form :input').val('');
            } else {
                alert("비밀번호가 일치하지 않습니다.");
                $('form :input').val('');
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            console.log("에러발생", errorThrown);
            alert(xhr.responseText);
        }
    });
}