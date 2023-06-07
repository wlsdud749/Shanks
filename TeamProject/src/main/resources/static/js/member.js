var currentPage = 0; // 현재 페이지 변수를 추가합니다.
var page = 0;
var search = "";
$(document).ready(function () {
    // 페이지 로드 시 게시글 목록을 비동기적으로 가져오는 함수 호출
    loadArticles(page, search); // 초기 페이지를 0으로 설정
    $("#search").on("input", function() {
        search = $(this).val();

        // 게시글 목록 로드
        loadArticles(page, search);
    });
    
    $(document).on('change', '#flexRadioDefault1', function () {
        var isChecked = $(this).prop('checked');
        // 모든 체크 박스들의 상태 변경
        $('#article-list input[type="checkbox"]').prop('checked', isChecked);
        var checkedValues = getCheckedValues();
    });

    $(document).on("click", "#deleteBtn", function() {
        var confirmDelete = confirm("정말 삭제하시겠습니까?");
        if (confirmDelete) {
          var checkedValues = getCheckedValues();
          if (checkedValues != null && checkedValues.length != 0) {
            var deleteCount = 0;
            var totalDeleteCount = checkedValues.length;
      
            for (var i = 0; i < checkedValues.length; i++) {
              var member_idx = checkedValues[i];
              $.ajax({
                url: "/profile/member/delete",
                type: "GET",
                data: { member_idx : member_idx },
                success: function(response) {
                  deleteCount++;
      
                  if (deleteCount === totalDeleteCount) {
                    alert(response);
                    window.location.href = '/profile/member';
                  }
                },
                error: function(xhr) {
                  console.error(xhr.responseText);
                },
              });
            }
          } else {
            alert('삭제할 댓글을 선택해주세요.');
          }
        }
      });
});

function loadArticles(page, search) {
    $.ajax({
        url: '/profile/member/update', // 댓글 목록을 가져올 API 엔드포인트의 URL
        type: 'GET',
        data: {
            page: page,
            search: search
        },
        success: function (response) {
            // 성공적으로 응답을 받았을 때 처리하는 로직
            // 서버에서 받은 데이터를 사용하여 게시글 목록을 업데이트하는 등의 작업을 수행
            updateArticleList(response.content); // response를 사용하여 게시글 목록 업데이트
            updatePagination(page, response.totalPages); // 페이지네이션 업데이트
        },
        error: function (xhr) {
            // 요청이 실패했을 때 처리하는 로직
            console.error(xhr.responseText);
        }
    });
}

function updateArticleList(memberList) {
    var tbody = $("#article-list");
    var thead = $("#table-contents");
    tbody.empty();
    thead.empty();
        // 헤더 행 추가
    var headerRow = $('<tr></tr>');
    headerRow.append($('<th scope="col"><input class="form-check-input" type="checkbox" name="flexRadioDefault" id="flexRadioDefault1"></th>'));
    headerRow.append($('<th scope="col">Id</th>'));
    headerRow.append($('<th scope="col">Nick</th>'));
    headerRow.append($('<th scope="col">Email</th>'));
    headerRow.append($('<th scope="col">PhoneNumber</th>'));
    headerRow.append($('<th scope="col">가입날짜</th>'));
    thead.append(headerRow);
    
    for (var i = 0; i < memberList.length; i++) {
        (function () {
            var member = memberList[i];
            var member_idx = member.member_idx;
            var row = $('<tr></tr>');
            var checkbox = $('<input>').attr({
                'type': 'checkbox',
                'name': 'board-checkbox',
                'value':member_idx,
                'class': 'checkbox-css' // CSS class for the checkbox
            });
            var checkboxCell = $('<th></th>').append(checkbox);
            row.append(checkboxCell);
            row.append($('<td></td>').text(member.id));
            row.append($('<td></td>').text(member.nick));
            row.append($('<td></td>').text(member.email));
            row.append($('<td></td>').text(member.phoneNumber));
            row.append($('<td></td>').text(moment(member.regTime).format('YYYY-MM-DD HH:mm')));

            // 체크 박스 클릭 이벤트 처리
            checkbox.on('click', function () {
                var checkedValues = getCheckedValues(); // getCheckedValues 함수를 호출하여 선택된 값들을 가져옵니다.
            });

            tbody.append(row);
        })();
    }
}

function updatePagination(page, totalPages) {
    var pagination = $('.pagination');
    pagination.empty();

    var maxPageButtons = 5; // 최대 페이지 버튼 수를 설정합니다.
    var startPage = Math.max(page - Math.floor(maxPageButtons / 2), 0);
    var endPage = Math.min(startPage + maxPageButtons - 1, totalPages - 1); // endPage 값을 수정합니다.
  
    // Previous Page Button
    var previousButton = $('<li class="page-item"><a class="page-link" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>');
    previousButton.on('click', function() {
      if (currentPage > 0) {
        currentPage -= 5; // 이전 페이지로 이동하는 경우 5페이지씩 감소시킵니다.
        loadArticles(currentPage, search);
      }
    });
    pagination.append(previousButton);

    // Page Buttons
    for (var i = startPage; i <= endPage; i++) {
        (function (page) {
            var pageNumber = page + 1;
            var pageButton = $('<li class="page-item"><a class="page-link"></a></li>');
            pageButton.find('.page-link').text(pageNumber).attr('data-page', page).on('click', function () {
                var clickedPage = parseInt($(this).attr('data-page'));
                loadArticles(clickedPage, search);
            });
            pagination.append(pageButton);
        })(i);
    }

    // Next Page Button
    var nextButton = $('<li class="page-item"><a class="page-link" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>');
    nextButton.on('click', function () {
        if (currentPage + maxPageButtons < totalPages) {
            currentPage += 5; // 다음 페이지로 이동하는 경우 5페이지씩 증가시킵니다.
            loadArticles(currentPage, search);
        }
    });
    pagination.append(nextButton);
}

function getCheckedValues() {
    var checkedValues = [];
    $('#article-list input[type="checkbox"]:checked').each(function () {
        checkedValues.push(parseInt($(this).val()));
    });
    return checkedValues;
}