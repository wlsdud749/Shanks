var board_idx = $("#board_idx").text();
var id = "";
var role = "";
$(document).ready(function () {
  $.ajax({
    type: "GET",
    url: "/sign/username",
    success: function(response) {
      id = response;
    },
    error: function(xhr, status, error) {
      console.error(xhr.responseText);
    }
  });

  $.ajax({
    type: "GET",
    url: "/sign/userRole",
    success: function(response) {
      role = response;
    },
    error: function(xhr, status, error) {
      console.error(xhr.responseText);
    }
  });

  $.ajax({
    type: "GET",
    url: "/board/detail/count",
    data: { board_idx: board_idx },
    success: function (response) {
      $("#count").text(response);
    },
    error: function (xhr, status, error) {
      console.error(xhr.responseText);
    },
  });

  updateReviewList();

  $("#btndelete").click(function () {
    var confirmDelete = confirm("정말 삭제하시겠습니까?");
    if (confirmDelete) {
      // 삭제 요청을 서버로 보내는 코드 작성
      // $.ajax 또는 $.post 등을 사용하여 서버로 요청을 보낼 수 있습니다.
      $.ajax({
        type: "GET",
        url: "/board/delete",
        data: { board_idx: board_idx },
        success: function (response) {
          alert(response);
          window.location.href = "/board/list";
        },
        error: function (xhr, status, error) {
          console.error(xhr.responseText);
        },
      });
    }
  });

  $("#saveBtn").click(function () {
    var review = $("textarea.form-control").val(); // 입력된 댓글 내용
    // 입력된 댓글이 비어있는지 확인
    if (review.trim() === "") {
      alert("댓글을 입력하세요.");
      return;
    }
    // Ajax 요청을 보내고 댓글을 서버에 저장
    $.ajax({
      url: "/board/review", // 댓글을 저장할 엔드포인트 URL
      type: "POST",
      data: { 
        review: review, 
        board_idx : board_idx
      }, // 서버에 전송할 데이터
      success: function (response) {
        if(response == "댓글이 저장되었습니다."){
          alert(response);
          // 댓글 작성 후 입력 필드 초기화
          $("textarea.form-control").val("");
          updateReviewList();
        } else {
          alert("로그인 후 이용해주세요.");
          window.location.href = "/sign/sign-in";
        }
      },
      error: function (xhr) {
        console.error(xhr.responseText);
      }
    });
  });
});

function addReviewItem(review) {
  var reviewItem = $('<li class="list-group-item d-flex justify-content-between"></li>');
  reviewItem.attr('id', 'review-' + review.review_idx);

  var contentDiv = $('<div></div>').text(review.contents);
  reviewItem.append(contentDiv);
  var infoDiv = $('<div class="d-flex"></div>');
  if(id == review.memberDto.id) {
    infoDiv.append($('<div class="me-3"></div>').html("<strong>" + "작성자" + "</strong>"));
  } else {
    infoDiv.append($('<div class="me-3"></div>').text(review.memberDto.nick));
  }
  // 추출한 시간 값만 표시
  var regTime = review.regTime.split('T')[1].substring(0, 5);
  infoDiv.append($('<div class="me-3"></div>').text('작성시간: ' + regTime));

  if (id == review.memberDto.id) {
    var modifyButton = $('<button class="btn btn-primary btn-sm me-2">수정</button>');
    modifyButton.on('click', function() {
      // 수정 기능을 수행하는 함수 호출
      modifyTextArea(review);
    });
    infoDiv.append(modifyButton);
  }

  if(id == review.memberDto.id || role == "ADMIN") {
    var deleteButton = $('<button class="btn btn-danger btn-sm">삭제</button>');
    deleteButton.on('click', function() {
      // 삭제 기능을 수행하는 함수 호출
      deleteReview(review);
    });
    infoDiv.append(deleteButton);
  }

  reviewItem.append(infoDiv);
  $("#contents").append(reviewItem);
}

function updateReviewList() {
  $.ajax({
    type: "GET",
    url: "/board/review/view",
    data: { board_idx: board_idx },
    success: function (reviewDtos) {
      $("#contents").empty(); // 기존 댓글 목록 비우기
      if (reviewDtos != null && reviewDtos.length != 0) {
        for (var i = 0; i < reviewDtos.length; i++) {
          var review = reviewDtos[i];
          addReviewItem(review);
        }
      }
    },
    error: function (xhr) {
      console.error(xhr.responseText);
    }
  });
}

function modifyTextArea(review) {
  var review_idx = review.review_idx;
  var reviewItem = $("#review-" + review_idx);
  reviewItem.empty();

  var textareaDiv = $('<div class="col-10"></div>');
  var textarea = $('<textarea class="form-control"></textarea>');
  textarea.val(review.contents);
  textareaDiv.append(textarea);
  reviewItem.append(textareaDiv);

  var buttonDiv = $('<div class="d-flex"></div>');
  var modifyButton = $('<button class="btn btn-primary btn-sm me-2">수정</button>');
  var cancelButton = $('<button class="btn btn-danger btn-sm">취소</button>');
  buttonDiv.append(modifyButton);
  buttonDiv.append(cancelButton);
  reviewItem.append(buttonDiv);

  // 수정 버튼 클릭 시 이벤트 처리
  modifyButton.on('click', function () {
    var updateContents = textarea.val();
    modifyReview(review, updateContents);
  });

  // 취소 버튼 클릭 시 이벤트 처리
  cancelButton.on('click', function () {
    updateReviewItem(review); // 댓글 아이템을 다시 표시
  });
}

function updateReviewItem(review) {
  var review_idx = review.review_idx;
  var reviewItem = $("#review-" + review_idx);
  reviewItem.empty();

  var contentDiv = $('<div></div>').text(review.contents);
  reviewItem.append(contentDiv);
  var infoDiv = $('<div class="d-flex"></div>');
  if (id == review.memberDto.id) {
    infoDiv.append($('<div class="me-3"></div>').html("<strong>" + "작성자" + "</strong>"));
  } else {
    infoDiv.append($('<div class="me-3"></div>').text(review.memberDto.nick));
  }
  // 추출한 시간 값만 표시
  var regTime = review.regTime.split('T')[1].substring(0, 5);
  infoDiv.append($('<div class="me-3"></div>').text('작성시간: ' + regTime));

  if (id == review.memberDto.id) {
    var modifyButton = $('<button class="btn btn-primary btn-sm me-2">수정</button>');
    modifyButton.on('click', function () {
      // 수정 기능을 수행하는 함수 호출
      modifyTextArea(review);
    });
    infoDiv.append(modifyButton);

    var deleteButton = $('<button class="btn btn-danger btn-sm">삭제</button>');
    deleteButton.on('click', function () {
      // 삭제 기능을 수행하는 함수 호출
      deleteReview(review);
    });
    infoDiv.append(deleteButton);
  }

  reviewItem.append(infoDiv);
}

function modifyReview(review, updateContents) {
  if(id == review.memberDto.id) {
    var review_idx = review.review_idx;
    $.ajax({
      url: "/board/review/update", 
      type: "POST",
      data: {
        review_idx: review_idx,
        updateContents: updateContents
      }, 
      success: function (response) {
        alert(response);
        updateReviewList();
      },
      error: function (xhr) {
        console.error(xhr.responseText);
      }
    });
  } else {
    alert("로그인 후 이용해주세요.");
    window.location.href = "/sign/sign-in";
  }
}

function deleteReview(review) {
  var confirmDelete = confirm("정말 삭제하시겠습니까?");
    if (confirmDelete) {
      if (id == review.memberDto.id || role == "ADMIN") {
        var review_idx = review.review_idx;
        $.ajax({
          url: "/board/review/delete",
          type: "POST",
          data: {
            review_idx: review_idx
          },
          success: function (response) {
            alert(response);
            updateReviewList();
          },
          error: function (xhr) {
            console.error(xhr.responseText);
          }
        });
      } else {
        alert("로그인 후 이용해주세요.");
        window.location.href = "/sign/sign-in";
      }
    }
}