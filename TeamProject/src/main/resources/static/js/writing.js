$(document).ready(function () {
    $('#summernote').summernote({
        height: 600,                 // 에디터 높이
        minHeight: null,             // 최소 높이
        maxHeight: null,             // 최대 높이
        focus: true,                  // 에디터 로딩후 포커스를 맞출지 여부
        lang: "ko-KR",					// 한글 설정
        placeholder: '내용을 입력하세요.',	//placeholder 설정
        toolbar: [
            ['fontsize', ['fontsize']],
            ['style', ['bold', 'italic', 'underline', 'strikethrough', 'clear']],
            ['color', ['forecolor', 'color']],
            ['table', ['table']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['insert', ['picture', 'link']],
            ['view', ['fullscreen', 'help']]
        ],
        callbacks: {
            onImageUpload: function (files) {
                // 파일 업로드(다중업로드를 위해 반복문 사용)
                for (var i = files.length - 1; i >= 0; i--) {
                    uploadSummernoteImageFiles([files[i]], this);
                }
            },
            onMediaDelete: function (target) {
                deleteSummernoteImageFile(target[0].src);
                console.log(target[0].src);
            }
        }
    });
    function uploadSummernoteImageFiles(files, editor) {
        var formData = new FormData();
        for (var i = 0; i < files.length; i++) {
            formData.append("files", files[i]);
        }

        $.ajax({
            data: formData,
            type: "POST",
            url: "/board/uploadSummernoteImageFiles",
            contentType: false,
            processData: false,
            success: function (data) {
                for (var i = 0; i < data.length; i++) {
                    var imageUrl = data[i];
                    $(editor).summernote('insertImage', imageUrl);
                }
            },
            error: function (xhr, status, error) {
                console.error('이미지 업로드 실패');
                console.error(error);
            }
        });
    }

    function deleteSummernoteImageFile(imageUrl) {
        $.ajax({
          type: 'POST',
          url: '/board/deleteSummernoteImageFile',
          data: { imageUrl: imageUrl },
          success: function (response) {
            console.log('이미지 삭제 성공');
          },
          error: function (xhr, status, error) {
            console.error('이미지 삭제 실패');
            console.error(xhr.responseText);
          }
        });
      }

    $('#btnSave').click(function () {
        var title = $('#title').val();
        var contents = $('#summernote').summernote('code');
        var category = $('#category').val();
        if (title === '') {
            alert("제목을 입력하세요.");
            return;
        }
        if (contents === '<p><br></p>') {
            alert("내용을 입력하세요.");
            return;
        }

        var boardType = 'GENERAL';
        var currentURL = window.location.href;
        if (currentURL.includes('/board/writing/notice')) {
            boardType = 'NOTICE';
            title = "[공지] " + title;
        }

        var imageDtos = [];

        var imgTags = $(contents).find('img');

        imgTags.each(function() {
            var imgSrc = $(this).attr('src');
            var imgName = imgSrc.substring(imgSrc.lastIndexOf('/') + 1);
            imageDtos.push({ imgName: imgName });
          });
        
        // BoardDto 생성
        var boardDto = {
            title: title,
            contents: contents,
            category: category,
            count: 0,
            imageDtos: imageDtos,
            boardType: boardType
        };

        // Ajax를 이용하여 게시글 저장 요청
        $.ajax({
            type: 'POST',
            url: '/board/saveBoard/',
            contentType: 'application/json',
            data: JSON.stringify(boardDto),
            success: function (response) {
                // 저장 성공 시 처리
                alert('게시글이 성공적으로 저장되었습니다.');
                // 게시글 저장 후 페이지 이동
                window.location.href = '/board/list';
            },
            error: function (xhr, status, error) {
                console.error('게시글 저장 실패');
                console.error(xhr.responseText);
            }
        });
    });

    $('#btnList').click(function () {
        // 목록으로 이동하는 처리
        window.location.href = '/board/list';
    });

    // 페이지 벗어날때 게시물이 저장되지 않으면 로컬에 저장된 이미지들 삭제
    $(window).on('beforeunload', function () {
        $.ajax({
            type:"POST",
            url: "/board/deleteimage",
            success: function (response) {
                console.log('사진 삭제 성공');
            },
            error: function (xhr, status, error) {
                console.error('사진 삭제 실패');
                console.error(xhr.responseText);
            }
        });
    });
});