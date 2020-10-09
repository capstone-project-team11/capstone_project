using System.Collections;
using System.Collections.Generic;
using UnityEngine;


//캡슐의 head 부분의 모션
public class h_active : MonoBehaviour
{
    public int speed;
    private float time;

    void Update()
    {
        //시간의 경과에 따라 조건 발생
        time += Time.deltaTime;

        //tail과 반대방향으로 회전, z축으로 이동
        if (time <= 0.6)
        {
            transform.Rotate(new Vector3(0, 0, speed * Time.deltaTime * 35));
            transform.Translate(new Vector3(0, 0, Time.deltaTime/4));
        }

        //y축으로 회전하며 head가 열리는 모션
        else if (time <= 0.8)
        {
            transform.Rotate(new Vector3(0, speed * Time.deltaTime * 6, 0));
            //transform.Translate(new Vector3(0, 0, speed * Time.deltaTime / 6));
        }
    }
}