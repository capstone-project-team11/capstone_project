using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using static System.Math;
using System.IO;
using System.Net;
using UnityEngine.Networking;
using Newtonsoft.Json.Linq;
using System;

public class Rendering : MonoBehaviour
{
    public GameObject capsule;

    private double current_latitude;
    private double current_longitude;
    private double origin_latitude;
    private double origin_longitude;

    private List<int> capsuleName = new List<int>();
    private string[] capsule_name = { };

    private double time;

    private void Start()
    {
        origin_latitude = Input.location.lastData.latitude;
        origin_longitude = Input.location.lastData.longitude;
        current_latitude = origin_latitude;
        current_longitude = origin_longitude;
        StartCoroutine(GetCapsuleData());
        time = 0;
    }

    private void Update()
    {
        time += Time.deltaTime;

        if(time > 2)
        {
            GameObject.Find("Text").GetComponent<Text>().text = "";
        }

        if (time > 5)
        {
            current_latitude = Input.location.lastData.latitude;
            current_longitude = Input.location.lastData.longitude;

            //이동 거리가 5미터가 넘으면 캡슐 렌더링
            if (HaversineAlgorigm(origin_latitude, origin_longitude, current_latitude, current_longitude) > 5)
            {
                StartCoroutine(GetCapsuleData());
                StopCoroutine(GetCapsuleData());
                origin_latitude = current_latitude;
                origin_longitude = current_longitude;
                GameObject.Find("Text").GetComponent<Text>().text = "Update complete";
            }
            time = 0;
        }
    }

    //캡슐 데이터를 json 형식으로 불러온 뒤 생성
    IEnumerator GetCapsuleData()
    {
        string[] tmp_string;

        //rest api를 이용해 json 형식으로 캡슐 데이터 호출
        UnityWebRequest www = UnityWebRequest.Get("http://59.13.134.140:7070/capsules/location?lat=" + current_latitude + "&lng=" + current_longitude);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            //다운 받은 데이터 양식을 수정 및 객체별 배열 input
            var count = 0;
            string tmp = www.downloadHandler.text;
            tmp = tmp.Replace("[", "");
            tmp = tmp.Replace("]", "");
            tmp_string = tmp.Split('}');

            string[] json = new string[(tmp_string.Length - 1) / 2];

            //newtonsoft.json 형식으로 변환
            for (int i = 0; i < tmp_string.Length - 1; i += 2)
            {
                if (i == 0)
                {
                    json[count] = tmp_string[i] + '}' + tmp_string[i + 1] + '}';
                    count++;
                }
                else
                {
                    tmp_string[i] = tmp_string[i].Substring(1);
                    json[count] = tmp_string[i] + '}' + tmp_string[i + 1] + '}';
                    count++;
                }
            }

            int[] tmp_c_name = new int[json.Length];
            int[] copyCapsuleName = capsuleName.ToArray();

            //json 형태의 데이터를 이용하여 중복 검사 및 캡슐 생성 함수 호출
            for (int j = 0; j < json.Length; j++)
            {
                JObject jobj = JObject.Parse(json[j]);

                tmp_c_name[j] = Convert.ToInt32(jobj["capsule_id"]);

                //생성된 캡슐 중 중복되는 캡슐의 번호 확인(없으면 생성)
                if (System.Array.IndexOf(capsuleName.ToArray(), tmp_c_name[j]) == -1)
                {
                    var input_longitude = System.Convert.ToDouble(jobj["location"]["x"]);
                    var input_latitude = System.Convert.ToDouble(jobj["location"]["y"]);
                    GPSconvert(input_longitude, input_latitude, tmp_c_name[j].ToString());
                    capsuleName.Add(tmp_c_name[j]);
                }
            }

            //중복 제거
            for(int h = 0; h < copyCapsuleName.Length; h++)
            {
                if(System.Array.IndexOf(tmp_c_name, copyCapsuleName[h]) == -1)
                {
                    capsuleName.Remove(copyCapsuleName[h]);
                }
            }
        }
    }

    //타겟 캡슐의 좌표를 유니티 상의 좌표로 변환
    public void GPSconvert(double target_longitude, double target_latitude, string userID)
    {
        double distance_x;
        double distance_y;

        distance_x = HaversineAlgorigm(current_latitude, current_longitude, current_latitude, target_longitude);
        distance_y = HaversineAlgorigm(current_latitude, current_longitude, target_latitude, current_longitude);

        //현재 자이로스코프 오류로 앱 실행시 정면을 기준으로 하며 평면 좌표계를 이용하여 원점을
        //사용자의 위치로 캡슐의 상대적인 위치를 구함(단위 m)
        if (current_longitude - target_longitude >= 0)
        {
            if (current_latitude - target_latitude >= 0)//제3사분면
            {
                distance_x *= -1;
                distance_y *= -1;
            }
            else if (current_latitude - target_latitude < 0)//제2사분면
            {
                distance_x *= -1;
            }
        }

        else if (current_longitude - target_longitude < 0)
        {
            if (current_latitude - target_latitude >= 0)//제4사분면
            {
                distance_y *= -1;
            }
        }

        //캡슐을 해당위치에 생성
        var tmpCapsule = Instantiate(capsule, new Vector3((float)distance_x, 0, (float)distance_y), Quaternion.identity);
        tmpCapsule.name = userID;
    }

    //하버신 알고리즘으로 GPS 좌표 간의 거리를 계산
    private double HaversineAlgorigm(double lat1, double lon1, double lat2, double lon2)
    {
        double theta, distance;

        theta = lon1 - lon2;
        distance = (Sin(lat1 * PI / 180) * Sin(lat2 * PI / 180)) + (Cos(lat1 * PI / 180) * Cos(lat2 * PI / 180) * Cos(theta * PI / 180));
        distance = Acos(distance);
        distance = distance * 180 / PI;
        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344 * 1000;

        return distance;
    }
}
