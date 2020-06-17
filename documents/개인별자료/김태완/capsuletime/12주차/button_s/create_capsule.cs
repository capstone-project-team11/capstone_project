using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;


//캡슐 생성 클래스
public class create_capsule : MonoBehaviour
{
    //prefab을 추가할 Gameobject 선언
    public GameObject capsule;
    private GameObject cancel_button;
    private GameObject fix_button;
    private GameObject create_button;
    private GameObject t_texture;
    private Transform t_camera;

    private void Start()
    {
        //사용할 변수 초기화
        t_texture = GameObject.Find("Canvas").transform.Find("tmp_capsule_texture").gameObject;
        cancel_button = GameObject.Find("Canvas").transform.Find("cancel_button").gameObject;
        create_button = GameObject.Find("Canvas").transform.Find("create_button").gameObject;
        t_camera = GameObject.Find("First Person Camera").transform;
    }

    public void tmpobject()
    {
        //new 버튼 클릭시 활성화 및 비활성화 목록
        GameObject.Find("group").transform.GetChild(1).gameObject.SetActive(true);
        t_texture.SetActive(true);
        cancel_button.SetActive(true);
        create_button.SetActive(false);
    }

    public void newobject()
    {
        //임시 캡슐이 활성화된 상태
        if (cancel_button.activeSelf == true)
        {
            //capsule의 동적 생성 위치를 Canvas 하위로 배정한 뒤 명칭을 test_capsule로 수정
            //기존 capsule 생성시 특정 위치에만 생성되는 상태를 현 카메라 위치와 방향을 기준으로 일정거리 앞에 생성되게 변경
            var targetpositon = GameObject.Find("targetpo").transform;
            var c_child = Instantiate(capsule);
            c_child.transform.position = new Vector3(targetpositon.position.x, targetpositon.position.y, targetpositon.position.z);
            GameObject.Find("capsule(Clone)").name = "test_capsule";

            //capsule 생성 후 기본 화면으로 전환
            cancelobejct();
        }       
    }

    public void cancelobejct()
    {
        //cancel 버튼 클릭시 활성화 및 비활성화 목록
        GameObject.Find("group").transform.GetChild(1).gameObject.SetActive(false);
        t_texture.SetActive(false);
        cancel_button.SetActive(false);
        create_button.SetActive(true);
    }
}