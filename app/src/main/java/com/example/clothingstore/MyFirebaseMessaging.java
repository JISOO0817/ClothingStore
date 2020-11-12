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

    private static final String NOTIFICATION_CHANNER_ID = "MY_NOTIFICATION_ID"; //android o 나 그 위에는 이게 필요함

    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //모든 알림들은 여기로 받음


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // 알림으로부터 데이터 가져옴
        String notificatioType = remoteMessage.getData().get("notificationType");
        if(notificatioType.equals("주문이 들어왔어요.")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if(user != null && auth.getUid().equals(sellerUid)){
                //사용자가 로그인되어 있으며 알림 보낼 사용자와 동일함 (판매자)
                showNotification(orderId,sellerUid,buyerUid,notificationTitle,notificationDescription,notificatioType);
            }
        }
        if (notificatioType.equals("변경 완료되었습니다.")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationMessage");

            if(user != null && auth.getUid().equals( buyerUid)){
                //사용자가 로그인되어 있으며 알림 보낼 사용자와 동일함 (판매자)
                showNotification(orderId,sellerUid,buyerUid,notificationTitle,notificationDescription,notificatioType);
            }


        }
    }

    private void showNotification(String orderId, String sellerUid, String buyerUid, String notificationTitle, String notificationDescription,String notificationType) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        //알림을 위한 아이디를 랜덤으로

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupNoriciationChannel(notificationManager);
        }


        //알림 클릭하면 액티비티 시작

        Intent intent = null;
        if(notificationType.equals("주문이 들어왔어요.")){
            // 판매자 액티비티

            intent = new Intent(this, OrderDetailsSellerActivity.class);
            intent.putExtra("orderId",orderId);
            intent.putExtra("orderBy",buyerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }else if(notificationType.equals("변경 완료되었습니다.")){

            //구매자 액티비티

            intent = new Intent(this, OrderDetailsUserActivity.class);
            intent.putExtra("orderId",orderId);
            intent.putExtra("orderTo",sellerUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.icon);

        //알림 소리

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNER_ID);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNoriciationChannel(NotificationManager notificationManager) {

        CharSequence channelName = " 텍스트 ";
        String channelDescription = " 채널 설명";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNER_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if(notificationManager != null){
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }
}
