// Generator : SpinalHDL v1.7.3    git head : aeaeece704fe43c766e0d36a93f2ecbb8a9f2003
// Component : MsfReference
// Git hash  : d760d7c759943ec084b005722cdddca0f54f015e

`timescale 1ns/1ps

module MsfReference (
  output              io_led,
  input               clk,
  input               resetn
);

  wire                bufferCC_1_io_dataOut;
  wire                sanitisedClockArea_core_led;
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
    .led   (sanitisedClockArea_core_led), //o
    .clk   (clk                        ), //i
    .reset (_zz_reset_4                )  //i
  );
  assign reset = bufferCC_1_io_dataOut;
  always @(*) begin
    _zz_reset = 1'b0;
    _zz_reset = ((! reset) && (! _zz_reset_3));
  end

  assign _zz_reset_3 = (_zz_reset_2 == 16'hffff);
  always @(*) begin
    _zz_reset_1 = (_zz_reset_2 + _zz__zz_reset_1);
    if(1'b0) begin
      _zz_reset_1 = 16'h0;
    end
  end

  assign _zz_reset_4 = (reset || _zz_reset);
  assign io_led = sanitisedClockArea_core_led;
  always @(posedge clk) begin
    if(reset) begin
      _zz_reset_2 <= 16'h0;
    end else begin
      _zz_reset_2 <= _zz_reset_1;
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

  assign io_dataOut = buffers_1;
  always @(posedge clk or negedge resetn) begin
    if(!resetn) begin
      buffers_0 <= 1'b1;
      buffers_1 <= 1'b1;
    end else begin
      buffers_0 <= io_dataIn;
      buffers_1 <= buffers_0;
    end
  end


endmodule
