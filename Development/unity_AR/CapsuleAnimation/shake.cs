using System.Collections;
using System.Collections.Generic;
using UnityEngine;

//캡슐이 흔들림 모션을 취함
public class shake : MonoBehaviour
{
    private int value;
    private float time;

    void Update()
    {
        time += Time.deltaTime;
        if (time < 1.5)
        {
            rechange();
        }
        else
        {
            //var camera_y = GameObject.Find("First Person Camera").transform.rotation.y;
            //transform.rotation = Quaternion.Euler(0, -camera_y, 0);
            transform.LookAt(GameObject.Find("First Person Camera").transform);
            gameObject.GetComponent<shake>().enabled = false;
            transform.GetChild(0).transform.GetComponent<h_active>().enabled = true;
            transform.GetChild(1).transform.GetComponent<t_active>().enabled = true;
        }
    }
    void rechange()
    {
        value = Random.Range(-10, 10);
        transform.rotation = Quaternion.Euler(0, 180, value);
    }
}