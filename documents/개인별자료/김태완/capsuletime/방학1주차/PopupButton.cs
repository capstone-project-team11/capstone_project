using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PopupButton : MonoBehaviour
{
    //popup창 비활성화
    public void ClosePopup()
    {
        var images = GameObject.Find("images").transform;

        //생성된 이미지 삭제
        for(int i=0;i< images.childCount; i++)
        {
            Destroy(GameObject.Find("image"+i));
        }

        GameObject.Find("popup").transform.GetChild(3).gameObject.SetActive(false);
        GameObject.Find("popup").SetActive(false);
    }

    //이미지 전환(다음)
    public void next()
    {
        var obj = GameObject.Find("images").transform;
        int c_num = obj.childCount;
        obj.GetChild(c_num - 1).transform.SetAsFirstSibling();
    }

    //이미지 전환(이전)
    public void pre()
    {
        var obj = GameObject.Find("images").transform;
        int c_num = obj.childCount;
        obj.GetChild(0).transform.SetAsLastSibling();
    }
}
