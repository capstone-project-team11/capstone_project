using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

//팝업창에 표시할 사용자 이미지 및 동영상 출력
public class create_content : MonoBehaviour
{
    public RawImage rawimage;
    public RawImage video;

    //db 연결시 수정 필요
    void Start()
    {       
        string[] content = new string [6];

        //사진 및 비디오를 넣을 부모 위치 선언
        var i_parent = GameObject.Find("picture").transform;
        int count = 0;

        //정의된 배열의 길이만큼 반복
        foreach (string tmp in content)
        {
            if (count == 0)
            {
                var i_child = Instantiate(video);
                i_child.transform.SetParent(i_parent);
                i_child.transform.localPosition = new Vector3(0, 0, 0);
                GameObject.Find("video(Clone)").name = "video";
            }

            else
            {
                var i_child = Instantiate(rawimage);
                i_child.transform.SetParent(i_parent);
                i_child.transform.localPosition = new Vector3(0, 0, 0);
                GameObject.Find("RawImage(Clone)").name = "Image" + count;
            }            
            count += 1;
        }
    }
}
