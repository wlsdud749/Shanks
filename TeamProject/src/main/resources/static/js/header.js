$(function () {
    $("#signOut").click(function () {
        console.log("실행확인");
        $.ajax({
            type: "POST",
            url: "/sign/logout",
            success: function () {
                console.log("성공");
                document.location.reload();
            }
        });
    });
});

$(document).ready(function () {
    // 현재 페이지 URL을 가져옵니다.
    var currentPageUrl = window.location.href;

    // 게시판 페이지 링크 조건을 확인합니다.
    if (currentPageUrl.includes("/board") || currentPageUrl.includes("/sign/sign-up")) {
        $("#login-container").show();
    } else {
        $("#login-container").hide();
    }
});