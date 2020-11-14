package com.example.clothingstore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.clothingstore.activities.OrderDetailsSellerActivity;
import com.example.clothingstore.activities.OrderDetailsUserActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNER_ID = "MY_NOTIFICATION_ID"; //android o 나 그 윗 버전에서 필요

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //모든 알림들은 여기로 받음


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // 알림으로부터 데이터 가져옴
        String notificationType = remoteMessage.getData().get("notificationType");

        //onMessageReceived 메서드의 파라미터인 remoteMessage 를 이용하여 getData 로 데이터를 받아온다.
        //여기서 notificationType 은 키? 값이라고 할 수도 있겠다.


        if(notificationType.equals("판매자에게")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if(user != null && auth.getUid().equals(sellerUid)){
                //사용자가 로그인되어 있으며 알림 보낼 사용자와 동일함 (판매자)
                showNotification(orderId,sellerUid,buyerUid,notificationTitle,notificationDescription,notificationType);
            }
        }
        if (notificationType.equals("구매자에게")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if(user != null && auth.getUid().equals( buyerUid)){

                showNotification(orderId,sellerUid,buyerUid,notificationTitle,notificationDescription,notificationType);
            }


        }

        if (notificationType.equals("주문취소")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if(user != null && auth.getUid().equals( sellerUid)){
                //사용자가 로그인되어 있으며 알림 보낼 사용자와 동일함 (판매자)
                showNotification(orderId,sellerUid,buyerUid,notificationTitle,notificationDescription,notificationType);
            }


        }


    }

    private void showNotification(String orderId, String sellerUid, String buyerUid, String notificationTitle, String notificationDescription,String notificationType) {

        //알림을 관리하는 매니저객체
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //알림을 위한 아이디를 랜덤으로

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNoriciationChannel(notificationManager);
        }


        //알림 클릭하면 액티비티 시작

        Intent intent = null;
        if(notificationType.equals("판매자에게")){
            // 판매자 액티비티

            intent = new Intent(this, OrderDetailsSellerActivity.class);
            intent.putExtra("orderId",orderId);
            intent.putExtra("orderBy",buyerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }else if(notificationType.equals("구매자에게")){

            //구매자 액티비티

            intent = new Intent(this, OrderDetailsUserActivity.class);
            intent.putExtra("orderId",orderId);
            intent.putExtra("orderTo",sellerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }else if(notificationType.equals("주문취소")){

            //판매자 액티비티

            intent = new Intent(this,OrderDetailsSellerActivity.class);
            intent.putExtra("orderId",orderId);
            intent.putExtra("orderBy",buyerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        //pendingIntent : 특정 시점에 다른 컴포넌트에게 작업 요청시킬 때 사용
        //NotificationManager 가 인텐트 실행

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.icon);

        //알림 소리

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        //알림 만들기 (NotificationCompat API 사용)

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNER_ID);

        // NotificationCompat.Builder  생성자의 경우 채널 ID 를 제공해야 함 (Android 8.0) 이상에서는 호환성을 위해 필요

        notificationBuilder.setSmallIcon(R.drawable.icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationSoundUri)
                .setAutoCancel(true) //클릭했을 때 취소/사라짐
                .setContentIntent(pendingIntent); //인텐트 추가

        //알림 띄우기

        notificationManager.notify(notificationID,notificationBuilder.build());
    }



    //NotificationChannel 은 API 26부터 제공되므로 하위 버전에서 실행되지 않게
    // Build.VERSION.SDK_INT를 이용하여 버전 분기 프로그램을 작성해야 함

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNoriciationChannel(NotificationManager notificationManager) {

        CharSequence channelName = " 텍스트 ";  //알림채널 이름
        String channelDescription = " 채널 설명";  //

        //알림 채널 생성
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNER_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);  //진동
        if(notificationManager != null){
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
