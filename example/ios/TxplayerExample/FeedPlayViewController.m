//
//  FeedPlayViewController.m
//  TxplayerExample
//
//  Created by 邓锦龙 on 2023/8/3.
//

#import "FeedPlayViewController.h"
#import <TxplayerView.h>
#import <Masonry/Masonry.h>


@interface FeedPlayViewController ()
@property(strong, nonatomic) TxplayerView *playerView;
@end

@implementation FeedPlayViewController

- (void)viewDidLoad {
    [super viewDidLoad];
  self.playerView = [TxplayerView new];
  self.playerView.videoURL = @"http://1304755944.vod2.myqcloud.com/272c7433vodsh1304755944/a05d8bde3270835009213675516/vbKvtvlpRrQA.mp4";
  [self.view addSubview:self.playerView];
  [self.playerView mas_makeConstraints:^(MASConstraintMaker *make) {
    make.centerY.equalTo(self.view.mas_centerY);
    make.height.mas_equalTo(200);
    make.left.equalTo(self.view.mas_left);
    make.right.equalTo(self.view.mas_right);
  }];
  
  [self.playerView startPlay];
}

- (void)viewDidAppear:(BOOL)animated {
  [super viewDidAppear:animated];
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
