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

    private void Update()
    {
        //터치발생
        if (Input.GetMouseButtonDown(0))
        {
            //스크린에서 터치가 발생한 좌표를 기준으로 가상의 선 생성
            ray = Camera.main.ScreenPointToRay(Input.mousePosition);

            //ray의 범위 안에 gameobject가 인식
            if (Physics.Raycast(ray, out hit, Mathf.Infinity))
            {
                //인식된 gameobject의 이름이 특정 명칭과 동일할 경우 이벤트 발생
                if (hit.transform.gameObject)
                {
                    hitCapsule = hit.transform.gameObject.name;
                    showPopup();
                }
            }

            else
            {
                hitCapsule = "-1";
            }
        }
    }

    //popup창 활성화
    private void showPopup()
    {
        var popup = GameObject.Find("Canvas").transform.GetChild(2).gameObject;
        popup.SetActive(true);
    }
}
