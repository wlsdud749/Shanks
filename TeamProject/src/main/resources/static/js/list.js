var currentPage = 0; // 현재 페이지 변수를 추가합니다.
var page = 0;
var categoryId = "all";
var search = "";
$(document).ready(function () {
    // 페이지 로드 시 게시글 목록을 비동기적으로 가져오는 함수 호출
    loadArticles(page, categoryId, search); // 초기 페이지를 0으로 설정

    // 탭 클릭 이벤트 처리
    $(".list-group-item").on("click", function () {
        categoryId = $(this).attr("id"); // 클릭한 탭의 id를 가져옴
        var categoryData = $(this).text(); // 클릭한 탭의 텍스트를 가져옴'
        var href = $(this).attr("href");

        // 선택된 탭 표시
        $(".list-group-item").removeClass("active");
        $(this).addClass("active");

        var tabContent = $(".tab-content");
        tabContent.empty();

        var tabPane = $('<div class="tab-pane fade show active"></div>');
        tabPane.attr('id', href);
        var h1 = $('<h1 class="h3 mb-3 fw-normal"></h1>').text(categoryData);
        tabPane.append(h1);
        tabContent.append(tabPane);

        // 게시글 목록 로드
        loadArticles(page, categoryId, search);
    });

    $("#search").on("input", function() {
        search = $(this).val();

        // 게시글 목록 로드
        loadArticles(page, categoryId, search);
    });
});


function loadArticles(page, categoryId, search) {
    $.ajax({
        url: '/board/list/update', // 게시글 목록을 가져올 API 엔드포인트의 URL
        type: 'GET',
        data: {
            page: page,
            categoryId:categoryId,
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

function updateArticleList(boardList) {
    var tbody = $("#article-list");
    tbody.empty();
    for (var i = 0; i < boardList.length; i++) {
        (function () {
            var board = boardList[i];
            var board_idx = board.board_idx;
            var row = $('<tr></tr>');
            row.append($('<th></th>').attr('scope', 'row').text(board.board_idx));
            row.append($('<td></td>').text(board.memberDto.nick));
            row.append($('<td></td>').text(board.category));
            if (board.boardType == "NOTICE") {
                row.append($('<td></td>').html('<strong>' + board.title + '</strong>').css({
                    'max-width': '100px',
                    'white-space': 'nowrap',
                    'overflow': 'hidden',
                    'text-overflow': 'ellipsis'
                }));
            } else {
                row.append($('<td></td>').text(board.title).css({
                    'max-width': '100px',
                    'white-space': 'nowrap',
                    'overflow': 'hidden',
                    'text-overflow': 'ellipsis'
                }));
            }
            row.append($('<td></td>').text(board.count));
            row.append($('<td></td>').text(moment(board.regTime).format('YYYY-MM-DD HH:mm')));

            // 클릭 이벤트 처리
            row.click(function () {
                // var board_idx = $(this).find('th').text();
                // 페이지 이동
                window.location.href = "/board/detail?board_idx=" + board_idx;
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
        loadArticles(currentPage, categoryId, search);
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
                loadArticles(clickedPage, categoryId, search);
            });
            pagination.append(pageButton);
        })(i);
    }

    // Next Page Button
    var nextButton = $('<li class="page-item"><a class="page-link" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>');
    nextButton.on('click', function () {
        if (currentPage + maxPageButtons < totalPages) {
            currentPage += 5; // 다음 페이지로 이동하는 경우 5페이지씩 증가시킵니다.
            loadArticles(currentPage, categoryId, search);
        }
    });
    pagination.append(nextButton);
}