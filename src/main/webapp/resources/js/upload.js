function uploadToServer (formObj) {

    console.log("upload to server......");
    console.log(formObj);

    return axios({
        method: 'post',
        url: '/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    }).then(function(response) {
        return response.data;
    });
}

function removeFileToServer(uuid, fileName){

    return axios.delete('/remove/' + uuid + '_' + fileName)
        .then(function(response) {
            return response.data;
        });
}