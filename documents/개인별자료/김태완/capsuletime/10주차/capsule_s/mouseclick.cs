using System.Collections;
using System.Collections.Generic;
using UnityEngine;

//캡슐 클릭시 발생하는 이벤트
public class mouseclick : MonoBehaviour
{
    private Ray ray;
    private RaycastHit hit;
    private int value;
    private float time;
    private GameObject child_h;
    private GameObject child_t;
    private GameObject popup_child;

    void Update()
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
                if (hit.transform.gameObject.name == "test_capsule")
                {
                    GameObject now_capsule = hit.transform.gameObject;
                    child_h = hit.transform.GetChild(0).gameObject;//인식된 캡슐의 head
                    child_t = hit.transform.GetChild(1).gameObject;//인식된 캡슐의 tail
                    now_capsule.GetComponent<b_active>().enabled = false;//캡슐의 기본 모션 종료
                    now_capsule.GetComponent<shake>().enabled = true;//캡슐의 떨림효과 진행
                    
                    //invoke 지연함수 (실행할 함수, 지연시킬 시간(s))
                    Invoke("active", 2);//캡슐의 head와 tail 모션 발생
                    Invoke("pop_active", 3);//popup 창 실행
                }
            }
        }
    }
    void active()
    {
        child_h.GetComponent<h_active>().enabled = true;
        child_t.GetComponent<t_active>().enabled = true;
    }
    void pop_active()
    {
        var now_pop = GameObject.Find("Canvas").gameObject;
        popup_child = now_pop.transform.GetChild(1).gameObject;
        popup_child.SetActive(true);
    }
}