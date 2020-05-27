//capsule head motion 정의
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class h_active : MonoBehaviour
{
    public int speed;
    private float time;
    // Start is called before the first frame update
    void Start()
    {

    }

    // Update is called once per frame
    void Update()
    {
        time += Time.deltaTime;
        //capsule의 기본 회전
        if (time <= 4)
        {
            transform.Rotate(new Vector3(0, 0, speed * Time.deltaTime));
        }
        else if (time <= 5)
        {
            
        }
        //capsule의 역회전 및 z축으로의 이동
        else if (time <= 5.6)
        {
            transform.Rotate(new Vector3(0, 0, speed * Time.deltaTime*33));
            transform.Translate(new Vector3(0, 0, Time.deltaTime));
        }
        //capsule의 각도와 위치 변경을 통해 head의 열림 효과 연출
        else if (time <= 5.8)
        {
            transform.Rotate(new Vector3(0, speed * Time.deltaTime * 6, 0));
            transform.Translate(new Vector3(0, 0, speed * Time.deltaTime/6));
        }
    }
}
