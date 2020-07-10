namespace GoogleARCore
{
#if UNITY_EDITOR
    using Input = InstantPreviewInput;
#endif

    using System.Collections;
    using System.Collections.Generic;
    using UnityEngine;
    using GoogleARCore;

    public class AnchorCreate : MonoBehaviour
    {
        public Camera FirstPersonCamera;

        private double time;
        private bool flag = false;

        private void Awake()
        {
            Application.targetFrameRate = 60;           
        }

        private void Start()
        {      
            time = 0;
        }

        private void Update()
        {
            //기본 Anchor 생성
            if(flag == false)
            {
                var tmpAnchor = Session.CreateAnchor(new Pose(FirstPersonCamera.transform.position, Quaternion.identity));
                GetComponent<Rendering>().enabled = true;
                flag = true;
            }

            time += Time.deltaTime;
            
            //Anchor 교체
            if (time > 10)
            {
                var tmpAnchor = Session.CreateAnchor(new Pose(FirstPersonCamera.transform.position, Quaternion.identity));
                time = 0;
            }
        }
    }
}
