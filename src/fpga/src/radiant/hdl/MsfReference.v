// Generator : SpinalHDL v1.8.0    git head : 4e3563a282582b41f4eaafc503787757251d23ea
// Component : MsfReference
// Git hash  : bf2d03836f804ec48cdffd5804bef8c7b1474914

`timescale 1ns/1ps

module MsfReference (
  output              io_p23,
  output              io_ledR,
  output              io_ledG,
  output              io_ledB,
  input               clk,
  input               resetn
);

  wire                bufferCC_1_io_dataOut;
  wire                sanitisedClockArea_core_p23;
  wire                sanitisedClockArea_core_ledR;
  wire                sanitisedClockArea_core_ledG;
  wire                sanitisedClockArea_core_ledB;
  wire       [15:0]   _zz__zz_reset_1;
  wire       [0:0]    _zz__zz_reset_1_1;
  wire                reset;
  reg                 _zz_reset;
  reg        [15:0]   _zz_reset_1;
  reg        [15:0]   _zz_reset_2;
  wire                _zz_reset_3;
  wire                _zz_reset_4;

  assign _zz__zz_reset_1_1 = _zz_reset;
  assign _zz__zz_reset_1 = {15'd0, _zz__zz_reset_1_1};
  BufferCC bufferCC_1 (
    .io_dataIn  (1'b0                 ), //i
    .io_dataOut (bufferCC_1_io_dataOut), //o
    .clk        (clk                  ), //i
    .resetn     (resetn               )  //i
  );
  Core sanitisedClockArea_core (
    .p23   (sanitisedClockArea_core_p23 ), //o
    .ledR  (sanitisedClockArea_core_ledR), //o
    .ledG  (sanitisedClockArea_core_ledG), //o
    .ledB  (sanitisedClockArea_core_ledB), //o
    .clk   (clk                         ), //i
    .reset (_zz_reset_4                 )  //i
  );
  assign reset = bufferCC_1_io_dataOut; // @[CrossClock.scala 13:9]
  always @(*) begin
    _zz_reset = 1'b0; // @[Utils.scala 536:23]
    _zz_reset = ((! reset) && (! _zz_reset_3)); // @[MsfReference.scala 22:44]
  end

  assign _zz_reset_3 = (_zz_reset_2 == 16'hffff); // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_reset_1 = (_zz_reset_2 + _zz__zz_reset_1); // @[Utils.scala 548:15]
    if(1'b0) begin
      _zz_reset_1 = 16'h0; // @[Utils.scala 558:15]
    end
  end

  assign _zz_reset_4 = (reset || _zz_reset); // @[BaseType.scala 305:24]
  assign io_p23 = sanitisedClockArea_core_p23; // @[MsfReference.scala 36:24]
  assign io_ledR = sanitisedClockArea_core_ledR; // @[MsfReference.scala 37:25]
  assign io_ledG = sanitisedClockArea_core_ledG; // @[MsfReference.scala 38:25]
  assign io_ledB = sanitisedClockArea_core_ledB; // @[MsfReference.scala 39:25]
  always @(posedge clk) begin
    if(reset) begin
      _zz_reset_2 <= 16'h0; // @[Data.scala 400:33]
    end else begin
      _zz_reset_2 <= _zz_reset_1; // @[Reg.scala 39:30]
    end
  end


endmodule

module BufferCC (
  input               io_dataIn,
  output              io_dataOut,
  input               clk,
  input               resetn
);

  (* async_reg = "true" *) reg                 buffers_0;
  (* async_reg = "true" *) reg                 buffers_1;

  assign io_dataOut = buffers_1; // @[CrossClock.scala 38:14]
  always @(posedge clk or negedge resetn) begin
    if(!resetn) begin
      buffers_0 <= 1'b1; // @[Data.scala 400:33]
      buffers_1 <= 1'b1; // @[Data.scala 400:33]
    end else begin
      buffers_0 <= io_dataIn; // @[CrossClock.scala 31:14]
      buffers_1 <= buffers_0; // @[CrossClock.scala 34:16]
    end
  end


endmodule
