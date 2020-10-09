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
    public Camera fcamera;

    //카메라 좌표
    private double origin_x;
    private double origin_z;

    //gps 좌표
    private double current_latitude;
    private double current_longitude;
    private double origin_latitude;
    private double origin_longitude;

    private float resultAngle;
    private bool flag_init = false;

    public static List<int> capsuleName = new List<int>();
    //private string[] capsule_name = {};

    private double time;

    private void Update()
    {
        if (flag_init == false)
        {
            setting();
            flag_init = true;
        }

        time += Time.deltaTime;

        //필터 활성화시 재렌더링 진행
        if (Button.reRenderSign == true)
        {
            Button.reRenderSign = false;
            StartCoroutine(GetCapsuleData());
            StopCoroutine(GetCapsuleData());
        }

        if (time > 2)
        {
            var current_x = fcamera.transform.position.x;
            var current_z = fcamera.transform.position.z;
            //이동 거리가 3미터가 넘으면 캡슐 렌더링
            if (Math.Pow(origin_x - current_x, 2) >= 9 || Math.Pow(origin_z - current_z, 2) >= 9)
            {
                origin_x = current_x;
                origin_z = current_z;
                current_latitude = Input.location.lastData.latitude;
                current_longitude = Input.location.lastData.longitude;
                StartCoroutine(GetCapsuleData());
                StopCoroutine(GetCapsuleData());
            }
            time = 0;
        }
    }

    private void setting()
    {
        //버튼 활성화
        GameObject.Find("Canvas").transform.GetChild(0).gameObject.SetActive(true);

        //초기 gps 좌표값 설정
        origin_latitude = Input.location.lastData.latitude;
        origin_longitude = Input.location.lastData.longitude;

        current_latitude = origin_latitude;
        current_longitude = origin_longitude;

        origin_x = 0;
        origin_z = 0;

        //자북극 각도
        Input.compass.enabled = true;
        resultAngle = Input.compass.trueHeading;

        //렌더링
        StartCoroutine(GetCapsuleData());
        time = 0;
    }

    //캡슐 데이터를 json 형식으로 불러온 뒤 생성
    IEnumerator GetCapsuleData()
    {
        string[] tmp_string;

        //rest api를 이용해 json 형식으로 캡슐 데이터 호출
        UnityWebRequest www = UnityWebRequest.Get(InformationData.serverUrl + "capsules/location?lat=" + current_latitude + "&lng=" + current_longitude);
        yield return www.SendWebRequest();

        if (www.isNetworkError || www.isHttpError)
        {
            Debug.Log(www.error);
        }
        else
        {
            if(www.downloadHandler.text.Length < 3)
            {
                print("not exist");
            }
            else
            {
                //다운 받은 데이터 양식을 수정 및 객체별 배열 input
                string tmp = www.downloadHandler.text;
                tmp = tmp.Substring(1, tmp.Length - 2);
                tmp = tmp.Replace("]},", "*");
                tmp_string = tmp.Split('*');

                for(int i = 0; i < tmp_string.Length; i++)
                {
                    if(i != tmp_string.Length - 1)
                    {
                        tmp_string[i] = tmp_string[i] + "]}";
                    }
                }

                int[] tmp_c_name = new int[tmp_string.Length];
                int[] copyCapsuleName = capsuleName.ToArray();

                //json 형태의 데이터를 이용하여 중복 검사 및 캡슐 생성 함수 호출
                for (int j = 0; j < tmp_string.Length; j++)
                {
                    JObject jobj = JObject.Parse(tmp_string[j]);
                    tmp_c_name[j] = Convert.ToInt32(jobj["capsule_id"]);

                    var renderFlag = checkRenderingCondition(jobj);

                    //생성된 캡슐 중 중복되는 캡슐의 번호 확인(없으면 생성)
                    if (System.Array.IndexOf(capsuleName.ToArray(), tmp_c_name[j]) == -1 && renderFlag != false)
                    {
                        var input_longitude = System.Convert.ToDouble(jobj["location"]["x"]);
                        var input_latitude = System.Convert.ToDouble(jobj["location"]["y"]);
                        var check_key = Convert.ToInt32(jobj["key_count"]);
                        var check_locked = Convert.ToInt32(jobj["status_lock"]);
                        var expire = jobj["expire"].ToString();
                        GPSconvert(input_longitude, input_latitude, tmp_c_name[j].ToString(), check_key, check_locked, expire);
                        capsuleName.Add(tmp_c_name[j]);
                    }
                }
            }
        }
    }

    private bool checkRenderingCondition(JObject target)
    {
        var checkFlag = true;

        //임시캡슐일 경우 랜더링x
        if (target["status_temp"].ToString() == "1")
        {
            checkFlag = false;
            return checkFlag;
        }

        //잠금캡슐의 경우 잠금캡슐 멤버일 때만 렌더링 진행
        if (target["status_lock"].ToString() == "1" && target["members"].ToString().Length > 3)
        {
            var memberKey = target["members"].ToString();
            memberKey = memberKey.Substring(1, memberKey.Length - 2);
            memberKey = memberKey.Replace("},", "*");
            var memberKeyList = memberKey.Split('*');
            
            for (int h = 0; h < memberKeyList.Length; h++)
            {
                if (h != memberKeyList.Length - 1)
                {
                    memberKeyList[h] += "}";
                }
            }

            var memberFlag = false;

            for (int z = 0; z < memberKeyList.Length; z++)
            {
                JObject tmpMember = JObject.Parse(memberKeyList[z]);
                if (tmpMember["nick_name"].ToString() == InformationData.userNickName)
                {
                    memberFlag = true;
                    break;
                }
            }

            return memberFlag;
        }

        //친구 필터 기능이 작동중인 경우 친구들만 출력
        if (Button.friendFilterSign == true)
        {
            var filterFlag = false;
            var followArray = InformationData.bothfollowName.ToArray();

            for (int h = 0; h < followArray.Length; h++)
            {
                if (target["nick_name"].ToString() == followArray[h])
                {
                    filterFlag = true;
                    break;
                }
            }
            checkFlag = filterFlag;
        }

        return checkFlag;
    }

    //타겟 캡슐의 좌표를 유니티 상의 좌표로 변환
    public void GPSconvert(double target_longitude, double target_latitude, string userID, int key, int locked, string dday)
    {
        double distance_x;
        double distance_y;

        distance_x = HaversineAlgorithm(current_latitude, current_longitude, current_latitude, target_longitude);
        distance_y = HaversineAlgorithm(current_latitude, current_longitude, target_latitude, current_longitude);

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
        if (key == 0 && locked == 0)
        {
            var tmpCapsule = Instantiate(capsule, new Vector3((float)distance_x + (float)fcamera.transform.position.x, 0, (float)distance_y + (float)fcamera.transform.position.z), Quaternion.identity);
            tmpCapsule.transform.RotateAround(Vector3.zero, Vector3.up, -resultAngle);
            tmpCapsule.name = userID;
        }
        else if (key != 0 && locked == 1)
        {
            var tmpCapsule = Instantiate(lock_capsule, new Vector3((float)distance_x + (float)fcamera.transform.position.x, 0, (float)distance_y + (float)fcamera.transform.position.z), Quaternion.identity);
            tmpCapsule.transform.RotateAround(Vector3.zero, Vector3.up, -resultAngle);

            DateTime time = Convert.ToDateTime(dday);
            TimeSpan resultTime = time - DateTime.UtcNow;
            if(resultTime.Days > 0)
            {
                tmpCapsule.GetComponent<Text>().text = "lock";
            }

            if (resultTime.Days <= 0)
            {
                if (resultTime.TotalSeconds > 0)
                {
                    tmpCapsule.GetComponent<Text>().text = "lock";
                }
                else
                {
                    tmpCapsule.GetComponent<Text>().text = "open";
                }
            }
            tmpCapsule.name = userID;
        }
    }

    //하버사인 알고리즘으로 GPS 좌표 간의 거리를 계산
    private double HaversineAlgorithm(double lat1, double lon1, double lat2, double lon2)
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
