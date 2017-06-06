package com.seunghoshin.android.viewpager_2;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class FourFragment extends Fragment implements OnMapReadyCallback {

    LocationManager manager;
    // 전역변수를 만들어 아래에도 참조할수 있게 만든다
    GoogleMap map = null;

    public FourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_four, container, false);
        // 프래그먼트 map(아이디 호출하기 , 프래그먼트를 맵뷰로 쓰겠다는 코드 , 프래그먼트 매니저를 통해서 아이디를 가져온다
        // 프래그먼트 안에서 프래그먼트를 가져올때 쓰는거임
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        // 맵이 사용할 준비가 되면 onMapReady 함수를 자동으로 호출된다
        mapFragment.getMapAsync(this);

        //프래그먼트가 호출된 상위 액티비티를 가져올수있음 (나를 호출한 액티비티를 가져옴) / getActivity는 나를 가지고있는 액티비티를 뜻한다
        //상위 액티비티의 자원을 사용하기 위해서 Activity를 가져온다
        MainActivity activity = (MainActivity) getActivity();
        manager = activity.getLocationManager();
        return view;
    }

    // 이 함수부터 맵을 그린다
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        //좌표 생성
        LatLng namsan = new LatLng(37.551968, 126.988500);
        //좌표 적용
        // 1.1 마커생성
        MarkerOptions marker = new MarkerOptions();
        marker.position(namsan); //좌표
        marker.title("남산타워");
        // 1.2 마커를 화면에 그림
        map.addMarker(marker);
        // 2. 맵의 중심을 해당 좌표로 이동                          좌표   , 줌레벨
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(namsan,16));
    }


    // 현재 프래그먼트가 러닝직전  , 생명주기를 생각하면 onResume에다가 해줘야 한다
    @Override
    public void onResume() {
        super.onResume();

        //마시멜로 이상버전에서는 런타임 권한 체크여부를 확인해야 한다
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            // GPS 사용을 위한 권한 휙득이 되어 있지 않으면 리스너 해제하지 않는다
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
        }
        // GPS 리스너 등록
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, //위치제공자
                3000, //변경사항 체크 주기 millisecond 단위임
                1, //변경사항 체크 거리 meter단위
                locationListener //나는 locationListener를 쓸꺼야
        );

    }

    // 현재 프래그먼트가 정지
    @Override
    public void onPause() {
        super.onPause();


        //마시멜로 이상버전에서는 런타임 권한 체크여부를 확인해야 한다
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            // GPS 사용을 위한 권한 휙득이 되어 있지 않으면 리스너를 해제하지 않는다
            if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                return;
            }
        }

        // 리스너 해제 , 프래그먼트 작동 해제
        manager.removeUpdates(locationListener);
    }

    //GPS 사용을 위해서 좌표 리스너를 생성
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //경도
            double lng = location.getLongitude();
            //위도
            double lat = location.getLatitude();
            //고도
            double alt = location.getAltitude();
            //정확도
            float acc = location.getAccuracy();
            //위치제공자(ISP)
            String provider = location.getProvider();

            //바뀐 현재 좌표
            LatLng current = new LatLng(lat,lng);
            //현재좌표로 카메라를 이동시킬때 // TODO 바로 여기에 전역변수 구글맵이 들어감
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,17));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 위치 공급자의 상태가 바뀔 때 호출 됩니다
        }

        @Override
        public void onProviderEnabled(String provider) {
            // 위치 공급자가 사용 가능해질(enabled) 때 호출 됩니다.
        }

        @Override
        public void onProviderDisabled(String provider) {
            //  위치 공급자가 사용 불가능해질(disabled) 때 호출 됩니다.
        }

    };
}
