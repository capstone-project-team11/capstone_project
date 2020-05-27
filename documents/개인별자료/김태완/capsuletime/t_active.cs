//capsule tail motion 정의
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class t_active : MonoBehaviour
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
            transform.Rotate(new Vector3(0, -speed * Time.deltaTime, 0));
        }
        else if (time <= 5)
        {
            
        }
        else if (time <= 5.6)
        {
            transform.Rotate(new Vector3(0, speed * Time.deltaTime/4, 0));
        }
        //capsule의 head 부분과 반대 방향으로 꺾이며 열림 효과 연출
        else if (time <= 5.8)
        {
            transform.Rotate(new Vector3(speed * Time.deltaTime * 4, 0, 0));
        }
    }
}
