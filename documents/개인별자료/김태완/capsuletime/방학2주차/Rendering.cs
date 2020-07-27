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
    public GameObject lock_capsule;
    //아래는 테스트 부분으로 이후 제거
    public GameObject angle;
    public GameObject text1;
    public Camera fcamera;

    //gps 좌표
    private double current_latitude;
    private double current_longitude;
    private double origin_latitude;
    private double origin_longitude;

    //사용자폰 회전각도
    private double first_latitude;
    private double first_longitude;
    private double second_latitude;
    private double second_longitude;

    private float resultAngle = 0;
    private bool flag;
    private bool flag_init = false;

    private List<int> capsuleName = new List<int>();
    private string[] capsule_name = { };

    private double time;

    private void Update()
    {
        if (flag_init == false)
        {
            setting();
            flag_init = true;
        }

        time += Time.deltaTime;

        if(flag == false && time > 3)
        {
            second_latitude = Input.location.lastData.latitude;
            second_longitude = Input.location.lastData.longitude;

            //사용자의 위치 이동이 확인되면 북쪽을 기준으로 변화 각도를 측정
            if (HaversineAlgorigm(first_latitude, first_longitude, second_latitude, second_longitude) > 1)
            {
                resultAngle = northAngle(first_latitude, first_longitude, second_latitude, second_longitude);
                angle.GetComponent<Text>().text = resultAngle.ToString();
                flag = true;
            }
        }

        if(time > 1)
        {
            GameObject.Find("Text").GetComponent<Text>().text = "";
        }

        if (time > 3)
        {
            current_latitude = Input.location.lastData.latitude;
            current_longitude = Input.location.lastData.longitude;

            //이동 거리가 3미터가 넘으면 캡슐 렌더링
            if (HaversineAlgorigm(origin_latitude, origin_longitude, current_latitude, current_longitude) > 3)
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

    private void setting()
    {
        //초기 gps 좌표값 설정
        origin_latitude = Input.location.lastData.latitude;
        origin_longitude = Input.location.lastData.longitude;

        current_latitude = origin_latitude;
        current_longitude = origin_longitude;

        first_latitude = origin_latitude;
        first_longitude = origin_longitude;

        //렌더링
        StartCoroutine(GetCapsuleData());
        time = 0;
        flag = false;
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
                    var check_key = Convert.ToInt32(jobj["key_count"]);
                    var check_locked = Convert.ToInt32(jobj["status_lock"]);
                    GPSconvert(input_longitude, input_latitude, tmp_c_name[j].ToString(), check_key, check_locked);
                    capsuleName.Add(tmp_c_name[j]);
                }
            }
        }
    }

    //타겟 캡슐의 좌표를 유니티 상의 좌표로 변환
    public void GPSconvert(double target_longitude, double target_latitude, string userID, int key, int locked)
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

        //캡슐을 해당위치에 생성(3d모델링 후 캡슐 생성은 통일, 캡슐 이름만 if문으로 작성)
        if (key == 0 && locked == 0)
        {
            var tmpCapsule = Instantiate(capsule, new Vector3((float)distance_x + (float)fcamera.transform.position.x, 0, (float)distance_y + (float)fcamera.transform.position.z), Quaternion.identity);
            //회전해줄 코드를 넣을 위치
            tmpCapsule.name = userID;
        }
        else if (key != 0 && locked == 0)
        {
            var tmpCapsule = Instantiate(lock_capsule, new Vector3((float)distance_x + (float)fcamera.transform.position.x, 0, (float)distance_y + (float)fcamera.transform.position.z), Quaternion.identity);
            //회전해줄 코드를 넣을 위치
            tmpCapsule.name = userID + "O";
        }
        else
        {
            var tmpCapsule = Instantiate(lock_capsule, new Vector3((float)distance_x + (float)fcamera.transform.position.x, 0, (float)distance_y + (float)fcamera.transform.position.z), Quaternion.identity);
            //회전해줄 코드를 넣을 위치
            tmpCapsule.name = userID + "L";
        }
        
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

    //사용자의 위치변화를 통해 북쪽과의 각도 계산(삼각형의 세 길이를 이용한 각 구하기)
    private float northAngle(double firstLat, double firstLon, double secondLat, double secondLon)
    {
        double result;

        if (firstLon == secondLon)
        {
            if (firstLat < secondLat)
            {
                result = 0;
            }
            else
            {
                result = 180;
            }
        }

        else {
            double standLat = firstLat + 0.000017;
            double standLon = firstLon;
            double disA = HaversineAlgorigm(firstLat, firstLon, standLat, standLon);
            double disB = HaversineAlgorigm(firstLat, firstLon, secondLat, secondLon);
            double disC = HaversineAlgorigm(secondLat, secondLon, standLat, standLon);

            result = Acos((float)(((disA*disA) + (disB*disB) - (disC*disC)) / (2 * disB * disA))) * 180 / PI;

            if(firstLon > secondLon)
            {
                result *= -1;
            }
        }

        return (float)result;
    }
}
