package xyz.tootal.contactsystem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final List<Person> personList=new ArrayList<>();
    private PersonAdapter personAdapter;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
//        personList.add(new Person("Police","110"));
        dbHelper=new MyDatabaseHelper(this,"Contact.db",null,1);
        db=dbHelper.getWritableDatabase();
        personAdapter=new PersonAdapter(MainActivity.this,R.layout.person_item,personList);
        initPersons();
        ListView listView=findViewById(R.id.list_view);
        listView.setAdapter(personAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Person person=personList.get(position);
//                Toast.makeText(MainActivity.this, person.getNumber(), Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,PersonDetailActivity.class);
                intent.putExtra("person",person);
                startActivity(intent);
            }
        });
    }

    private void initPersons(){
        Log.d(TAG, "initPersons: start");
        Cursor cursor=db.rawQuery("select name,number from person",null);
        if(cursor.moveToFirst()){
            do{
                String name=cursor.getString(cursor.getColumnIndex("name"));
                String number=cursor.getString(cursor.getColumnIndex("number"));
                Person person=new Person(name,number);
                personList.add(person);
            }while(cursor.moveToNext());
        }
        cursor.close();
        personAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add:
                Log.d(TAG, "onOptionsItemSelected: add");
                break;
            case R.id.menu_import:
//                Log.d(TAG, "onOptionsItemSelected: import");
                Intent intent=new Intent(MainActivity.this,ImportActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.menu_export:
                Log.d(TAG, "onOptionsItemSelected: export");
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "onActivityResult: begin");
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    Log.d(TAG, "onActivityResult: 1");
//                    Toast.makeText(this, "get "+data.getStringExtra("SystemContacts"), Toast.LENGTH_SHORT).show();
                    ArrayList systemContactList=(ArrayList<Person>) data.getSerializableExtra("SystemContacts");
                    for(int i=0;i<systemContactList.size();i++){
                        Person person=(Person)systemContactList.get(i);
                        db.execSQL("insert into person(name,number) values(?,?)",new String[]{person.getName(),person.getNumber()});
                    }
                    personList.addAll(systemContactList);
                    personAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onActivityResult: added");
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }
}
