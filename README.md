#Vyfe App

###Upload process

    curl -H "Authorization: bearer <token>" -H "Content-Type: application/json" -H "Accept: application/vnd.vimeo.*+json;version=3.4" -d '{"upload": {"approach": "tus","size": "<size>"}}' https://api.vimeo.com/me/videos
    
    curl -X PATCH -H "Tus-Resumable: 1.0.0" -H "Upload-Offset: 0" -H "Content-Type: application/offset+octet-stream" -H "Accept: application/vnd.vimeo.*+json;version=3.4" --data "@<absolutePath>" "<uploadUrl>" -i
    
    curl -H "Tus-Resumable: 1.0.0" -H "Accept: application/vnd.vimeo.*+json;version=3.4" "<uploadUrl>" -I
