using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Networking;
using System;

public class Button : MonoBehaviour
{
    private GameObject show;
    private GameObject exit;
    private GameObject skin;

    public static bool friendFilterSign = false;
    public static bool reRenderSign = false;

    private void Start()
    {
        show = GameObject.Find("button").transform.GetChild(0).gameObject;
        exit = GameObject.Find("button").transform.GetChild(1).gameObject;
        skin = GameObject.Find("Canvas").transform.GetChild(1).gameObject;
    }

    //unity 종료
    public void ClickExit()
    {
        Input.location.Stop();
        GameObject.Find("ARCore Device").SetActive(false);
        Application.Quit();
#if !UNITY_EDITOR
        System.Diagnostics.Process.GetCurrentProcess().Kill();
#endif
    }

    //임시 캡슐 미리보기
    public void ClickShow()
    {
        if(Popup.popupActiveFlag != true)
        {
            GameObject.Find("Canvas").transform.GetChild(5).gameObject.SetActive(true);
        }
    }

    //캡슐 필터 on
    public void friendFilterOn()
    {
        friendFilterSign = true;
        reRenderSign = true;
        GameObject.Find("filterOff").SetActive(false);
        GameObject.Find("button").transform.GetChild(2).gameObject.SetActive(true);
        removeCapsuleList();
    }

    //캡슐 필터 off
    public void friendFilterOff()
    {
        friendFilterSign = false;
        reRenderSign = true;
        GameObject.Find("filterOn").SetActive(false);
        GameObject.Find("button").transform.GetChild(3).gameObject.SetActive(true);
        removeCapsuleList();
    }

    //필터 기능에 부가적으로 사용되는 캡슐 삭제 기능
    private void removeCapsuleList()
    {
        //필터기능이 활성화 상태인 경우 현재 생성된 캡슐 전부 제거
        var tmpArray = Rendering.capsuleName.ToArray();
        for (int l = 0; l < tmpArray.Length; l++)
        {
            Destroy(GameObject.Find(tmpArray[l].ToString()).gameObject);
        }
        Rendering.capsuleName.Clear();
    }
}
