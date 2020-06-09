using System.Collections;
using System.Collections.Generic;
using UnityEngine;

//캡슐의 tail 부분의 모션
public class t_active : MonoBehaviour
{
    public int speed;
    private float time;

    void Update()
    {
        //시간의 경과에 따라 조건 발생
        time += Time.deltaTime;

        //head와 반대 방향으로 회전
        if (time <= 0.6)
        {
            transform.Rotate(new Vector3(0, speed * Time.deltaTime / 4, 0));
        }

        //x축으로 회전하며 tail이 열리는 모션
        else if (time <= 0.8)
        {
            transform.Rotate(new Vector3(speed * Time.deltaTime * 6, 0, 0));
        }
    }
}