using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CapsuleClick : MonoBehaviour
{
    private Ray ray;
    private RaycastHit hit;
    private GameObject popup_child;
    public static string hitCapsule;
    public static GameObject SaveCapsuleName;
    public Transform vrcamera;

    private void Update()
    {
        //터치발생
        if (Input.GetMouseButtonDown(0))
        {
            //스크린에서 터치가 발생한 좌표를 기준으로 가상의 선 생성
            ray = Camera.main.ScreenPointToRay(Input.mousePosition);

            //ray의 범위 안에 gameobject가 인식
            if (Physics.Raycast(ray, out hit, Mathf.Infinity) && Popup.popupActiveFlag != true && KeyList.keyPppupFlag != true)
            {
                //인식된 gameobject의 이름이 특정 명칭과 동일할 경우 이벤트 발생
                if (hit.transform.gameObject)
                {
                    if(hit.transform.gameObject.name != "Capsule")
                    {
                        hitCapsule = hit.transform.gameObject.name;
                        //잠금 캡슐의 경우 상태에 따라 팝업 반응 분할
                        if (hit.transform.gameObject.GetComponent<Text>().text == "lock")
                        {
                            showLockPopup();
                        }
                        else if (hit.transform.gameObject.GetComponent<Text>().text == "open")
                        {
                            showExpiredPopup();
                        }
                        else
                        {
                            hit.transform.gameObject.GetComponent<b_active>().enabled = false;
                            hit.transform.gameObject.GetComponent<shake>().enabled = true;
                        }
                    }
                }
            }
        }
    }

    //잠금캡슐 클릭팝업 활성화
    private void showLockPopup()
    {
        var lockPopup = GameObject.Find("Canvas").transform.GetChild(3).gameObject;
        lockPopup.SetActive(true);
        Invoke("offLockPopup", 2);
    }

    //잠금캡슐 클릭팝업 비활성화
    private void offLockPopup()
    {
        var lockPopup = GameObject.Find("Canvas").transform.GetChild(3).gameObject;
        lockPopup.SetActive(false);
    }

    //잠금캡슐active 클릭팝업 활성화
    private void showExpiredPopup()
    {
        var expiredPopup = GameObject.Find("Canvas").transform.GetChild(4).gameObject;
        expiredPopup.SetActive(true);
    }
}
