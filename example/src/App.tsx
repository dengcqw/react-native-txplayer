import * as React from 'react';
import { StyleSheet, View, Pressable, Text, Dimensions } from 'react-native';
import TxplayerView from '@jtreact/react-native-txplayer';

import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

const RootStack = createNativeStackNavigator<any>();

const screenSize = Dimensions.get('window');
const url = 'http://1304755944.vod2.myqcloud.com/272c7433vodsh1304755944/a05d8bde3270835009213675516/vbKvtvlpRrQA.mp4';

const RootStackScreen = () => {
  return (
    <NavigationContainer>
      <RootStack.Navigator>
        <RootStack.Screen name="App" component={App} />
      </RootStack.Navigator>
    </NavigationContainer>
  );
};

function App() {
  const playerRef = React.useRef();

  return (
    <View style={styles.container}>
      <TxplayerView ref={playerRef} videoURL={url} style={styles.box} onDownload={() => {
        console.warn('download');
      }}  />
      <Pressable
        style={styles.pressable}
        onPress={() => {
          playerRef.current.startPlay();
        }}
      >
        <Text>start</Text>
      </Pressable>
      <Pressable
        style={styles.pressable}
        onPress={() => {
          playerRef.current.addDanmaku(['弹幕1', '弹幕2']);
        }}
      >
        <Text>发送弹幕</Text>
      </Pressable>
    </View>
  );
}

export default RootStackScreen;

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: '100%',
    height: 300,
    marginVertical: 20,
  },
  pressable: {
    width: 100,
    height: 50,
    backgroundColor: 'gray',
  },
});
