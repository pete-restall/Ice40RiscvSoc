// Generator : SpinalHDL v1.8.0    git head : 4e3563a282582b41f4eaafc503787757251d23ea
// Component : Core
// Git hash  : 0e070ca66e014d5d14e8788f51469370f4b40ee1

`timescale 1ns/1ps

module Core (
  output              p23,
  output              ledR,
  output              ledG,
  output              ledB,
  input               clk,
  input               reset
);

  wire                cpu_1_interrupts_external;
  wire                cpu_1_interrupts_timer;
  wire                dbusSlaveMap_masters_0_slaveSelects_0;
  wire                dbusSlaveMap_masters_0_slaveSelects_1;
  wire                executableSlaveMap_masters_0_slaveSelects_0;
  wire                executableSlaveMap_masters_0_slaveSelects_1;
  wire                executableSlaveMap_masters_1_slaveSelects_0;
  wire                executableSlaveMap_masters_1_slaveSelects_1;
  wire                sharedSlaveArbiters_0_0_io_masters_0_request;
  wire                sharedSlaveArbiters_0_0_io_masters_1_request;
  wire                sharedSlaveArbiters_1_0_io_masters_0_request;
  wire                sharedSlaveArbiters_1_0_io_masters_1_request;
  wire       [31:0]   cpu_1_ibus_DAT_MOSI;
  wire       [29:0]   cpu_1_ibus_ADR;
  wire                cpu_1_ibus_CYC;
  wire       [3:0]    cpu_1_ibus_SEL;
  wire                cpu_1_ibus_STB;
  wire                cpu_1_ibus_WE;
  wire       [2:0]    cpu_1_ibus_CTI;
  wire       [1:0]    cpu_1_ibus_BTE;
  wire       [31:0]   cpu_1_dbus_DAT_MOSI;
  wire       [29:0]   cpu_1_dbus_ADR;
  wire                cpu_1_dbus_CYC;
  wire       [3:0]    cpu_1_dbus_SEL;
  wire                cpu_1_dbus_STB;
  wire                cpu_1_dbus_WE;
  wire       [2:0]    cpu_1_dbus_CTI;
  wire       [1:0]    cpu_1_dbus_BTE;
  wire       [31:0]   ledDevice_io_wishbone_DAT_MISO;
  wire                ledDevice_io_wishbone_ACK;
  wire                ledDevice_io_p23;
  wire                ledDevice_io_ledR;
  wire                ledDevice_io_ledG;
  wire                ledDevice_io_ledB;
  wire       [15:0]   ice40Ebram4k_2_DO_1;
  wire       [15:0]   lowInstructionEbram_wishbone_DAT_MISO;
  wire                lowInstructionEbram_wishbone_ACK;
  wire       [15:0]   lowInstructionEbram_ebram_DI;
  wire       [7:0]    lowInstructionEbram_ebram_ADW;
  wire       [7:0]    lowInstructionEbram_ebram_ADR;
  wire                lowInstructionEbram_ebram_CEW;
  wire                lowInstructionEbram_ebram_CER;
  wire                lowInstructionEbram_ebram_RE;
  wire                lowInstructionEbram_ebram_WE;
  wire       [15:0]   lowInstructionEbram_ebram_MASK_N;
  wire       [15:0]   ice40Ebram4k_3_DO_1;
  wire       [15:0]   highInstructionEbram_wishbone_DAT_MISO;
  wire                highInstructionEbram_wishbone_ACK;
  wire       [15:0]   highInstructionEbram_ebram_DI;
  wire       [7:0]    highInstructionEbram_ebram_ADW;
  wire       [7:0]    highInstructionEbram_ebram_ADR;
  wire                highInstructionEbram_ebram_CEW;
  wire                highInstructionEbram_ebram_CER;
  wire                highInstructionEbram_ebram_RE;
  wire                highInstructionEbram_ebram_WE;
  wire       [15:0]   highInstructionEbram_ebram_MASK_N;
  wire       [31:0]   wishboneBusDataExpander_2_master_DAT_MISO;
  wire                wishboneBusDataExpander_2_master_ACK;
  wire       [15:0]   wishboneBusDataExpander_2_slaves_0_DAT_MOSI;
  wire       [7:0]    wishboneBusDataExpander_2_slaves_0_ADR;
  wire                wishboneBusDataExpander_2_slaves_0_CYC;
  wire       [15:0]   wishboneBusDataExpander_2_slaves_0_SEL;
  wire                wishboneBusDataExpander_2_slaves_0_STB;
  wire                wishboneBusDataExpander_2_slaves_0_WE;
  wire       [15:0]   wishboneBusDataExpander_2_slaves_1_DAT_MOSI;
  wire       [7:0]    wishboneBusDataExpander_2_slaves_1_ADR;
  wire                wishboneBusDataExpander_2_slaves_1_CYC;
  wire       [15:0]   wishboneBusDataExpander_2_slaves_1_SEL;
  wire                wishboneBusDataExpander_2_slaves_1_STB;
  wire                wishboneBusDataExpander_2_slaves_1_WE;
  wire       [31:0]   wideInstructionEbramBlock_master_DAT_MISO;
  wire                wideInstructionEbramBlock_master_ACK;
  wire       [31:0]   wideInstructionEbramBlock_slave_DAT_MOSI;
  wire       [7:0]    wideInstructionEbramBlock_slave_ADR;
  wire                wideInstructionEbramBlock_slave_CYC;
  wire       [31:0]   wideInstructionEbramBlock_slave_SEL;
  wire                wideInstructionEbramBlock_slave_STB;
  wire                wideInstructionEbramBlock_slave_WE;
  wire       [31:0]   instructionEbramBlockWidthAdjusted_master_DAT_MISO;
  wire                instructionEbramBlockWidthAdjusted_master_ACK;
  wire       [31:0]   instructionEbramBlockWidthAdjusted_slave_DAT_MOSI;
  wire       [7:0]    instructionEbramBlockWidthAdjusted_slave_ADR;
  wire                instructionEbramBlockWidthAdjusted_slave_CYC;
  wire       [3:0]    instructionEbramBlockWidthAdjusted_slave_SEL;
  wire                instructionEbramBlockWidthAdjusted_slave_STB;
  wire                instructionEbramBlockWidthAdjusted_slave_WE;
  wire       [15:0]   dataSprams_0_DO;
  wire       [15:0]   dataSprams_1_DO;
  wire       [15:0]   dataSpramBlocks_0_wishbone_DAT_MISO;
  wire                dataSpramBlocks_0_wishbone_ACK;
  wire       [13:0]   dataSpramBlocks_0_spram_AD;
  wire       [15:0]   dataSpramBlocks_0_spram_DI;
  wire       [3:0]    dataSpramBlocks_0_spram_MASKWE;
  wire                dataSpramBlocks_0_spram_WE;
  wire                dataSpramBlocks_0_spram_CS;
  wire       [15:0]   dataSpramBlocks_1_wishbone_DAT_MISO;
  wire                dataSpramBlocks_1_wishbone_ACK;
  wire       [13:0]   dataSpramBlocks_1_spram_AD;
  wire       [15:0]   dataSpramBlocks_1_spram_DI;
  wire       [3:0]    dataSpramBlocks_1_spram_MASKWE;
  wire                dataSpramBlocks_1_spram_WE;
  wire                dataSpramBlocks_1_spram_CS;
  wire       [31:0]   wishboneBusDataExpander_3_master_DAT_MISO;
  wire                wishboneBusDataExpander_3_master_ACK;
  wire       [15:0]   wishboneBusDataExpander_3_slaves_0_DAT_MOSI;
  wire       [13:0]   wishboneBusDataExpander_3_slaves_0_ADR;
  wire                wishboneBusDataExpander_3_slaves_0_CYC;
  wire       [3:0]    wishboneBusDataExpander_3_slaves_0_SEL;
  wire                wishboneBusDataExpander_3_slaves_0_STB;
  wire                wishboneBusDataExpander_3_slaves_0_WE;
  wire       [15:0]   wishboneBusDataExpander_3_slaves_1_DAT_MOSI;
  wire       [13:0]   wishboneBusDataExpander_3_slaves_1_ADR;
  wire                wishboneBusDataExpander_3_slaves_1_CYC;
  wire       [3:0]    wishboneBusDataExpander_3_slaves_1_SEL;
  wire                wishboneBusDataExpander_3_slaves_1_STB;
  wire                wishboneBusDataExpander_3_slaves_1_WE;
  wire       [31:0]   wideDataSpramBlock_master_DAT_MISO;
  wire                wideDataSpramBlock_master_ACK;
  wire       [31:0]   wideDataSpramBlock_slave_DAT_MOSI;
  wire       [13:0]   wideDataSpramBlock_slave_ADR;
  wire                wideDataSpramBlock_slave_CYC;
  wire       [7:0]    wideDataSpramBlock_slave_SEL;
  wire                wideDataSpramBlock_slave_STB;
  wire                wideDataSpramBlock_slave_WE;
  wire       [31:0]   dataSpramBlockWidthAdjusted_master_DAT_MISO;
  wire                dataSpramBlockWidthAdjusted_master_ACK;
  wire       [31:0]   dataSpramBlockWidthAdjusted_slave_DAT_MOSI;
  wire       [13:0]   dataSpramBlockWidthAdjusted_slave_ADR;
  wire                dataSpramBlockWidthAdjusted_slave_CYC;
  wire       [3:0]    dataSpramBlockWidthAdjusted_slave_SEL;
  wire                dataSpramBlockWidthAdjusted_slave_STB;
  wire                dataSpramBlockWidthAdjusted_slave_WE;
  wire       [31:0]   ledDeviceWidthAdjusted_master_DAT_MISO;
  wire                ledDeviceWidthAdjusted_master_ACK;
  wire       [31:0]   ledDeviceWidthAdjusted_slave_DAT_MOSI;
  wire       [0:0]    ledDeviceWidthAdjusted_slave_ADR;
  wire                ledDeviceWidthAdjusted_slave_CYC;
  wire       [3:0]    ledDeviceWidthAdjusted_slave_SEL;
  wire                ledDeviceWidthAdjusted_slave_STB;
  wire                ledDeviceWidthAdjusted_slave_WE;
  wire       [31:0]   bridge_cpu_ibus_DAT_MISO;
  wire                bridge_cpu_ibus_ACK;
  wire                bridge_cpu_ibus_ERR;
  wire       [31:0]   bridge_cpu_dbus_DAT_MISO;
  wire                bridge_cpu_dbus_ACK;
  wire                bridge_cpu_dbus_ERR;
  wire       [31:0]   bridge_devices_ibus_DAT_MOSI;
  wire       [29:0]   bridge_devices_ibus_ADR;
  wire                bridge_devices_ibus_CYC;
  wire       [3:0]    bridge_devices_ibus_SEL;
  wire                bridge_devices_ibus_STB;
  wire                bridge_devices_ibus_WE;
  wire       [31:0]   bridge_devices_dataOnly_DAT_MOSI;
  wire       [29:0]   bridge_devices_dataOnly_ADR;
  wire                bridge_devices_dataOnly_CYC;
  wire       [3:0]    bridge_devices_dataOnly_SEL;
  wire                bridge_devices_dataOnly_STB;
  wire                bridge_devices_dataOnly_WE;
  wire       [31:0]   bridge_devices_dbusToExecutableBridge_DAT_MISO;
  wire                bridge_devices_dbusToExecutableBridge_ACK;
  wire       [31:0]   bridge_devices_executable_DAT_MOSI;
  wire       [29:0]   bridge_devices_executable_ADR;
  wire                bridge_devices_executable_CYC;
  wire       [3:0]    bridge_devices_executable_SEL;
  wire                bridge_devices_executable_STB;
  wire                bridge_devices_executable_WE;
  wire       [0:0]    dbusSlaveMap_masters_0_index;
  wire                dbusSlaveMap_masters_0_isValid;
  wire       [0:0]    executableSlaveMap_masters_0_index;
  wire                executableSlaveMap_masters_0_isValid;
  wire       [0:0]    executableSlaveMap_masters_1_index;
  wire                executableSlaveMap_masters_1_isValid;
  wire       [31:0]   dbusOnlySlaveMux_master_DAT_MISO;
  wire                dbusOnlySlaveMux_master_ACK;
  wire       [31:0]   dbusOnlySlaveMux_slaves_0_DAT_MOSI;
  wire       [29:0]   dbusOnlySlaveMux_slaves_0_ADR;
  wire                dbusOnlySlaveMux_slaves_0_CYC;
  wire       [3:0]    dbusOnlySlaveMux_slaves_0_SEL;
  wire                dbusOnlySlaveMux_slaves_0_STB;
  wire                dbusOnlySlaveMux_slaves_0_WE;
  wire       [31:0]   dbusOnlySlaveMux_slaves_1_DAT_MOSI;
  wire       [29:0]   dbusOnlySlaveMux_slaves_1_ADR;
  wire                dbusOnlySlaveMux_slaves_1_CYC;
  wire       [3:0]    dbusOnlySlaveMux_slaves_1_SEL;
  wire                dbusOnlySlaveMux_slaves_1_STB;
  wire                dbusOnlySlaveMux_slaves_1_WE;
  wire       [0:0]    priorityEncoder_2_output_1;
  wire                priorityEncoder_2_isValid;
  wire                sharedSlaveArbiters_0_0_io_encoder_inputs_0;
  wire                sharedSlaveArbiters_0_0_io_encoder_inputs_1;
  wire       [0:0]    sharedSlaveArbiters_0_0_io_grantedMasterIndex;
  wire                sharedSlaveArbiters_0_0_io_masters_0_isError;
  wire                sharedSlaveArbiters_0_0_io_masters_0_isStalled;
  wire                sharedSlaveArbiters_0_0_io_masters_0_isGranted;
  wire                sharedSlaveArbiters_0_0_io_masters_1_isError;
  wire                sharedSlaveArbiters_0_0_io_masters_1_isStalled;
  wire                sharedSlaveArbiters_0_0_io_masters_1_isGranted;
  wire       [0:0]    priorityEncoder_3_output_1;
  wire                priorityEncoder_3_isValid;
  wire                sharedSlaveArbiters_1_0_io_encoder_inputs_0;
  wire                sharedSlaveArbiters_1_0_io_encoder_inputs_1;
  wire       [0:0]    sharedSlaveArbiters_1_0_io_grantedMasterIndex;
  wire                sharedSlaveArbiters_1_0_io_masters_0_isError;
  wire                sharedSlaveArbiters_1_0_io_masters_0_isStalled;
  wire                sharedSlaveArbiters_1_0_io_masters_0_isGranted;
  wire                sharedSlaveArbiters_1_0_io_masters_1_isError;
  wire                sharedSlaveArbiters_1_0_io_masters_1_isStalled;
  wire                sharedSlaveArbiters_1_0_io_masters_1_isGranted;
  wire       [31:0]   masterMuxes_0_0_masters_0_DAT_MISO;
  wire                masterMuxes_0_0_masters_0_ACK;
  wire       [31:0]   masterMuxes_0_0_masters_1_DAT_MISO;
  wire                masterMuxes_0_0_masters_1_ACK;
  wire       [31:0]   masterMuxes_0_0_slave_DAT_MOSI;
  wire       [29:0]   masterMuxes_0_0_slave_ADR;
  wire                masterMuxes_0_0_slave_CYC;
  wire       [3:0]    masterMuxes_0_0_slave_SEL;
  wire                masterMuxes_0_0_slave_STB;
  wire                masterMuxes_0_0_slave_WE;
  wire       [31:0]   masterMuxes_1_0_masters_0_DAT_MISO;
  wire                masterMuxes_1_0_masters_0_ACK;
  wire       [31:0]   masterMuxes_1_0_masters_1_DAT_MISO;
  wire                masterMuxes_1_0_masters_1_ACK;
  wire       [31:0]   masterMuxes_1_0_slave_DAT_MOSI;
  wire       [29:0]   masterMuxes_1_0_slave_ADR;
  wire                masterMuxes_1_0_slave_CYC;
  wire       [3:0]    masterMuxes_1_0_slave_SEL;
  wire                masterMuxes_1_0_slave_STB;
  wire                masterMuxes_1_0_slave_WE;
  wire       [31:0]   wishboneBusSlaveMultiplexer_3_master_DAT_MISO;
  wire                wishboneBusSlaveMultiplexer_3_master_ACK;
  wire       [31:0]   wishboneBusSlaveMultiplexer_3_slaves_0_DAT_MOSI;
  wire       [29:0]   wishboneBusSlaveMultiplexer_3_slaves_0_ADR;
  wire                wishboneBusSlaveMultiplexer_3_slaves_0_CYC;
  wire       [3:0]    wishboneBusSlaveMultiplexer_3_slaves_0_SEL;
  wire                wishboneBusSlaveMultiplexer_3_slaves_0_STB;
  wire                wishboneBusSlaveMultiplexer_3_slaves_0_WE;
  wire       [31:0]   wishboneBusSlaveMultiplexer_3_slaves_1_DAT_MOSI;
  wire       [29:0]   wishboneBusSlaveMultiplexer_3_slaves_1_ADR;
  wire                wishboneBusSlaveMultiplexer_3_slaves_1_CYC;
  wire       [3:0]    wishboneBusSlaveMultiplexer_3_slaves_1_SEL;
  wire                wishboneBusSlaveMultiplexer_3_slaves_1_STB;
  wire                wishboneBusSlaveMultiplexer_3_slaves_1_WE;
  wire       [31:0]   wishboneBusSlaveMultiplexer_4_master_DAT_MISO;
  wire                wishboneBusSlaveMultiplexer_4_master_ACK;
  wire       [31:0]   wishboneBusSlaveMultiplexer_4_slaves_0_DAT_MOSI;
  wire       [29:0]   wishboneBusSlaveMultiplexer_4_slaves_0_ADR;
  wire                wishboneBusSlaveMultiplexer_4_slaves_0_CYC;
  wire       [3:0]    wishboneBusSlaveMultiplexer_4_slaves_0_SEL;
  wire                wishboneBusSlaveMultiplexer_4_slaves_0_STB;
  wire                wishboneBusSlaveMultiplexer_4_slaves_0_WE;
  wire       [31:0]   wishboneBusSlaveMultiplexer_4_slaves_1_DAT_MOSI;
  wire       [29:0]   wishboneBusSlaveMultiplexer_4_slaves_1_ADR;
  wire                wishboneBusSlaveMultiplexer_4_slaves_1_CYC;
  wire       [3:0]    wishboneBusSlaveMultiplexer_4_slaves_1_SEL;
  wire                wishboneBusSlaveMultiplexer_4_slaves_1_STB;
  wire                wishboneBusSlaveMultiplexer_4_slaves_1_WE;
  wire                masterMuxes_0_1_0_CYC;
  wire                masterMuxes_0_1_0_STB;
  wire                masterMuxes_0_1_0_ACK;
  wire                masterMuxes_0_1_0_WE;
  wire       [29:0]   masterMuxes_0_1_0_ADR;
  wire       [31:0]   masterMuxes_0_1_0_DAT_MISO;
  wire       [31:0]   masterMuxes_0_1_0_DAT_MOSI;
  wire       [3:0]    masterMuxes_0_1_0_SEL;
  wire                masterMuxes_0_1_1_CYC;
  wire                masterMuxes_0_1_1_STB;
  wire                masterMuxes_0_1_1_ACK;
  wire                masterMuxes_0_1_1_WE;
  wire       [29:0]   masterMuxes_0_1_1_ADR;
  wire       [31:0]   masterMuxes_0_1_1_DAT_MISO;
  wire       [31:0]   masterMuxes_0_1_1_DAT_MOSI;
  wire       [3:0]    masterMuxes_0_1_1_SEL;
  wire                masterMuxes_1_1_0_CYC;
  wire                masterMuxes_1_1_0_STB;
  wire                masterMuxes_1_1_0_ACK;
  wire                masterMuxes_1_1_0_WE;
  wire       [29:0]   masterMuxes_1_1_0_ADR;
  wire       [31:0]   masterMuxes_1_1_0_DAT_MISO;
  wire       [31:0]   masterMuxes_1_1_0_DAT_MOSI;
  wire       [3:0]    masterMuxes_1_1_0_SEL;
  wire                masterMuxes_1_1_1_CYC;
  wire                masterMuxes_1_1_1_STB;
  wire                masterMuxes_1_1_1_ACK;
  wire                masterMuxes_1_1_1_WE;
  wire       [29:0]   masterMuxes_1_1_1_ADR;
  wire       [31:0]   masterMuxes_1_1_1_DAT_MISO;
  wire       [31:0]   masterMuxes_1_1_1_DAT_MOSI;
  wire       [3:0]    masterMuxes_1_1_1_SEL;

  Cpu cpu_1 (
    .ibus_CYC            (cpu_1_ibus_CYC                ), //o
    .ibus_STB            (cpu_1_ibus_STB                ), //o
    .ibus_ACK            (bridge_cpu_ibus_ACK           ), //i
    .ibus_WE             (cpu_1_ibus_WE                 ), //o
    .ibus_ADR            (cpu_1_ibus_ADR[29:0]          ), //o
    .ibus_DAT_MISO       (bridge_cpu_ibus_DAT_MISO[31:0]), //i
    .ibus_DAT_MOSI       (cpu_1_ibus_DAT_MOSI[31:0]     ), //o
    .ibus_SEL            (cpu_1_ibus_SEL[3:0]           ), //o
    .ibus_ERR            (bridge_cpu_ibus_ERR           ), //i
    .ibus_CTI            (cpu_1_ibus_CTI[2:0]           ), //o
    .ibus_BTE            (cpu_1_ibus_BTE[1:0]           ), //o
    .dbus_CYC            (cpu_1_dbus_CYC                ), //o
    .dbus_STB            (cpu_1_dbus_STB                ), //o
    .dbus_ACK            (bridge_cpu_dbus_ACK           ), //i
    .dbus_WE             (cpu_1_dbus_WE                 ), //o
    .dbus_ADR            (cpu_1_dbus_ADR[29:0]          ), //o
    .dbus_DAT_MISO       (bridge_cpu_dbus_DAT_MISO[31:0]), //i
    .dbus_DAT_MOSI       (cpu_1_dbus_DAT_MOSI[31:0]     ), //o
    .dbus_SEL            (cpu_1_dbus_SEL[3:0]           ), //o
    .dbus_ERR            (bridge_cpu_dbus_ERR           ), //i
    .dbus_CTI            (cpu_1_dbus_CTI[2:0]           ), //o
    .dbus_BTE            (cpu_1_dbus_BTE[1:0]           ), //o
    .interrupts_external (cpu_1_interrupts_external     ), //i
    .interrupts_timer    (cpu_1_interrupts_timer        ), //i
    .clk                 (clk                           ), //i
    .reset               (reset                         )  //i
  );
  unamed ledDevice (
    .io_wishbone_CYC      (ledDeviceWidthAdjusted_slave_CYC           ), //i
    .io_wishbone_STB      (ledDeviceWidthAdjusted_slave_STB           ), //i
    .io_wishbone_ACK      (ledDevice_io_wishbone_ACK                  ), //o
    .io_wishbone_WE       (ledDeviceWidthAdjusted_slave_WE            ), //i
    .io_wishbone_ADR      (ledDeviceWidthAdjusted_slave_ADR           ), //i
    .io_wishbone_DAT_MISO (ledDevice_io_wishbone_DAT_MISO[31:0]       ), //o
    .io_wishbone_DAT_MOSI (ledDeviceWidthAdjusted_slave_DAT_MOSI[31:0]), //i
    .io_wishbone_SEL      (ledDeviceWidthAdjusted_slave_SEL[3:0]      ), //i
    .io_p23               (ledDevice_io_p23                           ), //o
    .io_ledR              (ledDevice_io_ledR                          ), //o
    .io_ledG              (ledDevice_io_ledG                          ), //o
    .io_ledB              (ledDevice_io_ledB                          ), //o
    .clk                  (clk                                        ), //i
    .reset                (reset                                      )  //i
  );
  Ice40Ebram4k_1 ice40Ebram4k_2 (
    .DI     (lowInstructionEbram_ebram_DI[15:0]    ), //i
    .ADW    (lowInstructionEbram_ebram_ADW[7:0]    ), //i
    .ADR    (lowInstructionEbram_ebram_ADR[7:0]    ), //i
    .CKW    (clk                                   ), //i
    .CKR    (clk                                   ), //i
    .CEW    (lowInstructionEbram_ebram_CEW         ), //i
    .CER    (lowInstructionEbram_ebram_CER         ), //i
    .RE     (lowInstructionEbram_ebram_RE          ), //i
    .WE     (lowInstructionEbram_ebram_WE          ), //i
    .MASK_N (lowInstructionEbram_ebram_MASK_N[15:0]), //i
    .DO_1   (ice40Ebram4k_2_DO_1[15:0]             )  //o
  );
  Ice40Ebram4k16WishboneBusAdapter_1 lowInstructionEbram (
    .wishbone_CYC      (wishboneBusDataExpander_2_slaves_0_CYC           ), //i
    .wishbone_STB      (wishboneBusDataExpander_2_slaves_0_STB           ), //i
    .wishbone_ACK      (lowInstructionEbram_wishbone_ACK                 ), //o
    .wishbone_WE       (wishboneBusDataExpander_2_slaves_0_WE            ), //i
    .wishbone_ADR      (wishboneBusDataExpander_2_slaves_0_ADR[7:0]      ), //i
    .wishbone_DAT_MISO (lowInstructionEbram_wishbone_DAT_MISO[15:0]      ), //o
    .wishbone_DAT_MOSI (wishboneBusDataExpander_2_slaves_0_DAT_MOSI[15:0]), //i
    .wishbone_SEL      (wishboneBusDataExpander_2_slaves_0_SEL[15:0]     ), //i
    .ebram_DI          (lowInstructionEbram_ebram_DI[15:0]               ), //o
    .ebram_ADW         (lowInstructionEbram_ebram_ADW[7:0]               ), //o
    .ebram_ADR         (lowInstructionEbram_ebram_ADR[7:0]               ), //o
    .ebram_CEW         (lowInstructionEbram_ebram_CEW                    ), //o
    .ebram_CER         (lowInstructionEbram_ebram_CER                    ), //o
    .ebram_RE          (lowInstructionEbram_ebram_RE                     ), //o
    .ebram_WE          (lowInstructionEbram_ebram_WE                     ), //o
    .ebram_MASK_N      (lowInstructionEbram_ebram_MASK_N[15:0]           ), //o
    .ebram_DO          (ice40Ebram4k_2_DO_1[15:0]                        ), //i
    .clk               (clk                                              ), //i
    .reset             (reset                                            )  //i
  );
  Ice40Ebram4k ice40Ebram4k_3 (
    .DI     (highInstructionEbram_ebram_DI[15:0]    ), //i
    .ADW    (highInstructionEbram_ebram_ADW[7:0]    ), //i
    .ADR    (highInstructionEbram_ebram_ADR[7:0]    ), //i
    .CKW    (clk                                    ), //i
    .CKR    (clk                                    ), //i
    .CEW    (highInstructionEbram_ebram_CEW         ), //i
    .CER    (highInstructionEbram_ebram_CER         ), //i
    .RE     (highInstructionEbram_ebram_RE          ), //i
    .WE     (highInstructionEbram_ebram_WE          ), //i
    .MASK_N (highInstructionEbram_ebram_MASK_N[15:0]), //i
    .DO_1   (ice40Ebram4k_3_DO_1[15:0]              )  //o
  );
  Ice40Ebram4k16WishboneBusAdapter_1 highInstructionEbram (
    .wishbone_CYC      (wishboneBusDataExpander_2_slaves_1_CYC           ), //i
    .wishbone_STB      (wishboneBusDataExpander_2_slaves_1_STB           ), //i
    .wishbone_ACK      (highInstructionEbram_wishbone_ACK                ), //o
    .wishbone_WE       (wishboneBusDataExpander_2_slaves_1_WE            ), //i
    .wishbone_ADR      (wishboneBusDataExpander_2_slaves_1_ADR[7:0]      ), //i
    .wishbone_DAT_MISO (highInstructionEbram_wishbone_DAT_MISO[15:0]     ), //o
    .wishbone_DAT_MOSI (wishboneBusDataExpander_2_slaves_1_DAT_MOSI[15:0]), //i
    .wishbone_SEL      (wishboneBusDataExpander_2_slaves_1_SEL[15:0]     ), //i
    .ebram_DI          (highInstructionEbram_ebram_DI[15:0]              ), //o
    .ebram_ADW         (highInstructionEbram_ebram_ADW[7:0]              ), //o
    .ebram_ADR         (highInstructionEbram_ebram_ADR[7:0]              ), //o
    .ebram_CEW         (highInstructionEbram_ebram_CEW                   ), //o
    .ebram_CER         (highInstructionEbram_ebram_CER                   ), //o
    .ebram_RE          (highInstructionEbram_ebram_RE                    ), //o
    .ebram_WE          (highInstructionEbram_ebram_WE                    ), //o
    .ebram_MASK_N      (highInstructionEbram_ebram_MASK_N[15:0]          ), //o
    .ebram_DO          (ice40Ebram4k_3_DO_1[15:0]                        ), //i
    .clk               (clk                                              ), //i
    .reset             (reset                                            )  //i
  );
  WishboneBusDataExpander_1 wishboneBusDataExpander_2 (
    .master_CYC        (wideInstructionEbramBlock_slave_CYC              ), //i
    .master_STB        (wideInstructionEbramBlock_slave_STB              ), //i
    .master_ACK        (wishboneBusDataExpander_2_master_ACK             ), //o
    .master_WE         (wideInstructionEbramBlock_slave_WE               ), //i
    .master_ADR        (wideInstructionEbramBlock_slave_ADR[7:0]         ), //i
    .master_DAT_MISO   (wishboneBusDataExpander_2_master_DAT_MISO[31:0]  ), //o
    .master_DAT_MOSI   (wideInstructionEbramBlock_slave_DAT_MOSI[31:0]   ), //i
    .master_SEL        (wideInstructionEbramBlock_slave_SEL[31:0]        ), //i
    .slaves_0_CYC      (wishboneBusDataExpander_2_slaves_0_CYC           ), //o
    .slaves_0_STB      (wishboneBusDataExpander_2_slaves_0_STB           ), //o
    .slaves_0_ACK      (lowInstructionEbram_wishbone_ACK                 ), //i
    .slaves_0_WE       (wishboneBusDataExpander_2_slaves_0_WE            ), //o
    .slaves_0_ADR      (wishboneBusDataExpander_2_slaves_0_ADR[7:0]      ), //o
    .slaves_0_DAT_MISO (lowInstructionEbram_wishbone_DAT_MISO[15:0]      ), //i
    .slaves_0_DAT_MOSI (wishboneBusDataExpander_2_slaves_0_DAT_MOSI[15:0]), //o
    .slaves_0_SEL      (wishboneBusDataExpander_2_slaves_0_SEL[15:0]     ), //o
    .slaves_1_CYC      (wishboneBusDataExpander_2_slaves_1_CYC           ), //o
    .slaves_1_STB      (wishboneBusDataExpander_2_slaves_1_STB           ), //o
    .slaves_1_ACK      (highInstructionEbram_wishbone_ACK                ), //i
    .slaves_1_WE       (wishboneBusDataExpander_2_slaves_1_WE            ), //o
    .slaves_1_ADR      (wishboneBusDataExpander_2_slaves_1_ADR[7:0]      ), //o
    .slaves_1_DAT_MISO (highInstructionEbram_wishbone_DAT_MISO[15:0]     ), //i
    .slaves_1_DAT_MOSI (wishboneBusDataExpander_2_slaves_1_DAT_MOSI[15:0]), //o
    .slaves_1_SEL      (wishboneBusDataExpander_2_slaves_1_SEL[15:0]     )  //o
  );
  WishboneBusSelMappingAdapter_1 wideInstructionEbramBlock (
    .master_CYC      (instructionEbramBlockWidthAdjusted_slave_CYC           ), //i
    .master_STB      (instructionEbramBlockWidthAdjusted_slave_STB           ), //i
    .master_ACK      (wideInstructionEbramBlock_master_ACK                   ), //o
    .master_WE       (instructionEbramBlockWidthAdjusted_slave_WE            ), //i
    .master_ADR      (instructionEbramBlockWidthAdjusted_slave_ADR[7:0]      ), //i
    .master_DAT_MISO (wideInstructionEbramBlock_master_DAT_MISO[31:0]        ), //o
    .master_DAT_MOSI (instructionEbramBlockWidthAdjusted_slave_DAT_MOSI[31:0]), //i
    .master_SEL      (instructionEbramBlockWidthAdjusted_slave_SEL[3:0]      ), //i
    .slave_CYC       (wideInstructionEbramBlock_slave_CYC                    ), //o
    .slave_STB       (wideInstructionEbramBlock_slave_STB                    ), //o
    .slave_ACK       (wishboneBusDataExpander_2_master_ACK                   ), //i
    .slave_WE        (wideInstructionEbramBlock_slave_WE                     ), //o
    .slave_ADR       (wideInstructionEbramBlock_slave_ADR[7:0]               ), //o
    .slave_DAT_MISO  (wishboneBusDataExpander_2_master_DAT_MISO[31:0]        ), //i
    .slave_DAT_MOSI  (wideInstructionEbramBlock_slave_DAT_MOSI[31:0]         ), //o
    .slave_SEL       (wideInstructionEbramBlock_slave_SEL[31:0]              )  //o
  );
  WishboneBusAddressMappingAdapter_2 instructionEbramBlockWidthAdjusted (
    .master_CYC      (masterMuxes_0_0_slave_CYC                               ), //i
    .master_STB      (masterMuxes_0_0_slave_STB                               ), //i
    .master_ACK      (instructionEbramBlockWidthAdjusted_master_ACK           ), //o
    .master_WE       (masterMuxes_0_0_slave_WE                                ), //i
    .master_ADR      (masterMuxes_0_0_slave_ADR[29:0]                         ), //i
    .master_DAT_MISO (instructionEbramBlockWidthAdjusted_master_DAT_MISO[31:0]), //o
    .master_DAT_MOSI (masterMuxes_0_0_slave_DAT_MOSI[31:0]                    ), //i
    .master_SEL      (masterMuxes_0_0_slave_SEL[3:0]                          ), //i
    .slave_CYC       (instructionEbramBlockWidthAdjusted_slave_CYC            ), //o
    .slave_STB       (instructionEbramBlockWidthAdjusted_slave_STB            ), //o
    .slave_ACK       (wideInstructionEbramBlock_master_ACK                    ), //i
    .slave_WE        (instructionEbramBlockWidthAdjusted_slave_WE             ), //o
    .slave_ADR       (instructionEbramBlockWidthAdjusted_slave_ADR[7:0]       ), //o
    .slave_DAT_MISO  (wideInstructionEbramBlock_master_DAT_MISO[31:0]         ), //i
    .slave_DAT_MOSI  (instructionEbramBlockWidthAdjusted_slave_DAT_MOSI[31:0] ), //o
    .slave_SEL       (instructionEbramBlockWidthAdjusted_slave_SEL[3:0]       )  //o
  );
  SP256K dataSprams_0 (
    .AD       (dataSpramBlocks_0_spram_AD[13:0]   ), //i
    .DI       (dataSpramBlocks_0_spram_DI[15:0]   ), //i
    .MASKWE   (dataSpramBlocks_0_spram_MASKWE[3:0]), //i
    .WE       (dataSpramBlocks_0_spram_WE         ), //i
    .CS       (dataSpramBlocks_0_spram_CS         ), //i
    .CK       (clk                                ), //i
    .STDBY    (1'b0                               ), //i
    .SLEEP    (1'b0                               ), //i
    .PWROFF_N (1'b1                               ), //i
    .DO       (dataSprams_0_DO[15:0]              )  //o
  );
  SP256K dataSprams_1 (
    .AD       (dataSpramBlocks_1_spram_AD[13:0]   ), //i
    .DI       (dataSpramBlocks_1_spram_DI[15:0]   ), //i
    .MASKWE   (dataSpramBlocks_1_spram_MASKWE[3:0]), //i
    .WE       (dataSpramBlocks_1_spram_WE         ), //i
    .CS       (dataSpramBlocks_1_spram_CS         ), //i
    .CK       (clk                                ), //i
    .STDBY    (1'b0                               ), //i
    .SLEEP    (1'b0                               ), //i
    .PWROFF_N (1'b1                               ), //i
    .DO       (dataSprams_1_DO[15:0]              )  //o
  );
  Ice40Spram16k16WishboneBusAdapter_1 dataSpramBlocks_0 (
    .wishbone_CYC      (wishboneBusDataExpander_3_slaves_0_CYC           ), //i
    .wishbone_STB      (wishboneBusDataExpander_3_slaves_0_STB           ), //i
    .wishbone_ACK      (dataSpramBlocks_0_wishbone_ACK                   ), //o
    .wishbone_WE       (wishboneBusDataExpander_3_slaves_0_WE            ), //i
    .wishbone_ADR      (wishboneBusDataExpander_3_slaves_0_ADR[13:0]     ), //i
    .wishbone_DAT_MISO (dataSpramBlocks_0_wishbone_DAT_MISO[15:0]        ), //o
    .wishbone_DAT_MOSI (wishboneBusDataExpander_3_slaves_0_DAT_MOSI[15:0]), //i
    .wishbone_SEL      (wishboneBusDataExpander_3_slaves_0_SEL[3:0]      ), //i
    .spram_AD          (dataSpramBlocks_0_spram_AD[13:0]                 ), //o
    .spram_DI          (dataSpramBlocks_0_spram_DI[15:0]                 ), //o
    .spram_MASKWE      (dataSpramBlocks_0_spram_MASKWE[3:0]              ), //o
    .spram_WE          (dataSpramBlocks_0_spram_WE                       ), //o
    .spram_CS          (dataSpramBlocks_0_spram_CS                       ), //o
    .spram_DO          (dataSprams_0_DO[15:0]                            ), //i
    .clk               (clk                                              ), //i
    .reset             (reset                                            )  //i
  );
  Ice40Spram16k16WishboneBusAdapter_1 dataSpramBlocks_1 (
    .wishbone_CYC      (wishboneBusDataExpander_3_slaves_1_CYC           ), //i
    .wishbone_STB      (wishboneBusDataExpander_3_slaves_1_STB           ), //i
    .wishbone_ACK      (dataSpramBlocks_1_wishbone_ACK                   ), //o
    .wishbone_WE       (wishboneBusDataExpander_3_slaves_1_WE            ), //i
    .wishbone_ADR      (wishboneBusDataExpander_3_slaves_1_ADR[13:0]     ), //i
    .wishbone_DAT_MISO (dataSpramBlocks_1_wishbone_DAT_MISO[15:0]        ), //o
    .wishbone_DAT_MOSI (wishboneBusDataExpander_3_slaves_1_DAT_MOSI[15:0]), //i
    .wishbone_SEL      (wishboneBusDataExpander_3_slaves_1_SEL[3:0]      ), //i
    .spram_AD          (dataSpramBlocks_1_spram_AD[13:0]                 ), //o
    .spram_DI          (dataSpramBlocks_1_spram_DI[15:0]                 ), //o
    .spram_MASKWE      (dataSpramBlocks_1_spram_MASKWE[3:0]              ), //o
    .spram_WE          (dataSpramBlocks_1_spram_WE                       ), //o
    .spram_CS          (dataSpramBlocks_1_spram_CS                       ), //o
    .spram_DO          (dataSprams_1_DO[15:0]                            ), //i
    .clk               (clk                                              ), //i
    .reset             (reset                                            )  //i
  );
  WishboneBusDataExpander wishboneBusDataExpander_3 (
    .master_CYC        (wideDataSpramBlock_slave_CYC                     ), //i
    .master_STB        (wideDataSpramBlock_slave_STB                     ), //i
    .master_ACK        (wishboneBusDataExpander_3_master_ACK             ), //o
    .master_WE         (wideDataSpramBlock_slave_WE                      ), //i
    .master_ADR        (wideDataSpramBlock_slave_ADR[13:0]               ), //i
    .master_DAT_MISO   (wishboneBusDataExpander_3_master_DAT_MISO[31:0]  ), //o
    .master_DAT_MOSI   (wideDataSpramBlock_slave_DAT_MOSI[31:0]          ), //i
    .master_SEL        (wideDataSpramBlock_slave_SEL[7:0]                ), //i
    .slaves_0_CYC      (wishboneBusDataExpander_3_slaves_0_CYC           ), //o
    .slaves_0_STB      (wishboneBusDataExpander_3_slaves_0_STB           ), //o
    .slaves_0_ACK      (dataSpramBlocks_0_wishbone_ACK                   ), //i
    .slaves_0_WE       (wishboneBusDataExpander_3_slaves_0_WE            ), //o
    .slaves_0_ADR      (wishboneBusDataExpander_3_slaves_0_ADR[13:0]     ), //o
    .slaves_0_DAT_MISO (dataSpramBlocks_0_wishbone_DAT_MISO[15:0]        ), //i
    .slaves_0_DAT_MOSI (wishboneBusDataExpander_3_slaves_0_DAT_MOSI[15:0]), //o
    .slaves_0_SEL      (wishboneBusDataExpander_3_slaves_0_SEL[3:0]      ), //o
    .slaves_1_CYC      (wishboneBusDataExpander_3_slaves_1_CYC           ), //o
    .slaves_1_STB      (wishboneBusDataExpander_3_slaves_1_STB           ), //o
    .slaves_1_ACK      (dataSpramBlocks_1_wishbone_ACK                   ), //i
    .slaves_1_WE       (wishboneBusDataExpander_3_slaves_1_WE            ), //o
    .slaves_1_ADR      (wishboneBusDataExpander_3_slaves_1_ADR[13:0]     ), //o
    .slaves_1_DAT_MISO (dataSpramBlocks_1_wishbone_DAT_MISO[15:0]        ), //i
    .slaves_1_DAT_MOSI (wishboneBusDataExpander_3_slaves_1_DAT_MOSI[15:0]), //o
    .slaves_1_SEL      (wishboneBusDataExpander_3_slaves_1_SEL[3:0]      )  //o
  );
  WishboneBusSelMappingAdapter wideDataSpramBlock (
    .master_CYC      (dataSpramBlockWidthAdjusted_slave_CYC           ), //i
    .master_STB      (dataSpramBlockWidthAdjusted_slave_STB           ), //i
    .master_ACK      (wideDataSpramBlock_master_ACK                   ), //o
    .master_WE       (dataSpramBlockWidthAdjusted_slave_WE            ), //i
    .master_ADR      (dataSpramBlockWidthAdjusted_slave_ADR[13:0]     ), //i
    .master_DAT_MISO (wideDataSpramBlock_master_DAT_MISO[31:0]        ), //o
    .master_DAT_MOSI (dataSpramBlockWidthAdjusted_slave_DAT_MOSI[31:0]), //i
    .master_SEL      (dataSpramBlockWidthAdjusted_slave_SEL[3:0]      ), //i
    .slave_CYC       (wideDataSpramBlock_slave_CYC                    ), //o
    .slave_STB       (wideDataSpramBlock_slave_STB                    ), //o
    .slave_ACK       (wishboneBusDataExpander_3_master_ACK            ), //i
    .slave_WE        (wideDataSpramBlock_slave_WE                     ), //o
    .slave_ADR       (wideDataSpramBlock_slave_ADR[13:0]              ), //o
    .slave_DAT_MISO  (wishboneBusDataExpander_3_master_DAT_MISO[31:0] ), //i
    .slave_DAT_MOSI  (wideDataSpramBlock_slave_DAT_MOSI[31:0]         ), //o
    .slave_SEL       (wideDataSpramBlock_slave_SEL[7:0]               )  //o
  );
  WishboneBusAddressMappingAdapter_1 dataSpramBlockWidthAdjusted (
    .master_CYC      (masterMuxes_1_0_slave_CYC                        ), //i
    .master_STB      (masterMuxes_1_0_slave_STB                        ), //i
    .master_ACK      (dataSpramBlockWidthAdjusted_master_ACK           ), //o
    .master_WE       (masterMuxes_1_0_slave_WE                         ), //i
    .master_ADR      (masterMuxes_1_0_slave_ADR[29:0]                  ), //i
    .master_DAT_MISO (dataSpramBlockWidthAdjusted_master_DAT_MISO[31:0]), //o
    .master_DAT_MOSI (masterMuxes_1_0_slave_DAT_MOSI[31:0]             ), //i
    .master_SEL      (masterMuxes_1_0_slave_SEL[3:0]                   ), //i
    .slave_CYC       (dataSpramBlockWidthAdjusted_slave_CYC            ), //o
    .slave_STB       (dataSpramBlockWidthAdjusted_slave_STB            ), //o
    .slave_ACK       (wideDataSpramBlock_master_ACK                    ), //i
    .slave_WE        (dataSpramBlockWidthAdjusted_slave_WE             ), //o
    .slave_ADR       (dataSpramBlockWidthAdjusted_slave_ADR[13:0]      ), //o
    .slave_DAT_MISO  (wideDataSpramBlock_master_DAT_MISO[31:0]         ), //i
    .slave_DAT_MOSI  (dataSpramBlockWidthAdjusted_slave_DAT_MOSI[31:0] ), //o
    .slave_SEL       (dataSpramBlockWidthAdjusted_slave_SEL[3:0]       )  //o
  );
  WishboneBusAddressMappingAdapter ledDeviceWidthAdjusted (
    .master_CYC      (dbusOnlySlaveMux_slaves_1_CYC               ), //i
    .master_STB      (dbusOnlySlaveMux_slaves_1_STB               ), //i
    .master_ACK      (ledDeviceWidthAdjusted_master_ACK           ), //o
    .master_WE       (dbusOnlySlaveMux_slaves_1_WE                ), //i
    .master_ADR      (dbusOnlySlaveMux_slaves_1_ADR[29:0]         ), //i
    .master_DAT_MISO (ledDeviceWidthAdjusted_master_DAT_MISO[31:0]), //o
    .master_DAT_MOSI (dbusOnlySlaveMux_slaves_1_DAT_MOSI[31:0]    ), //i
    .master_SEL      (dbusOnlySlaveMux_slaves_1_SEL[3:0]          ), //i
    .slave_CYC       (ledDeviceWidthAdjusted_slave_CYC            ), //o
    .slave_STB       (ledDeviceWidthAdjusted_slave_STB            ), //o
    .slave_ACK       (ledDevice_io_wishbone_ACK                   ), //i
    .slave_WE        (ledDeviceWidthAdjusted_slave_WE             ), //o
    .slave_ADR       (ledDeviceWidthAdjusted_slave_ADR            ), //o
    .slave_DAT_MISO  (ledDevice_io_wishbone_DAT_MISO[31:0]        ), //i
    .slave_DAT_MOSI  (ledDeviceWidthAdjusted_slave_DAT_MOSI[31:0] ), //o
    .slave_SEL       (ledDeviceWidthAdjusted_slave_SEL[3:0]       )  //o
  );
  CpuBusBridge bridge (
    .cpu_ibus_CYC                            (cpu_1_ibus_CYC                                      ), //i
    .cpu_ibus_STB                            (cpu_1_ibus_STB                                      ), //i
    .cpu_ibus_ACK                            (bridge_cpu_ibus_ACK                                 ), //o
    .cpu_ibus_WE                             (cpu_1_ibus_WE                                       ), //i
    .cpu_ibus_ADR                            (cpu_1_ibus_ADR[29:0]                                ), //i
    .cpu_ibus_DAT_MISO                       (bridge_cpu_ibus_DAT_MISO[31:0]                      ), //o
    .cpu_ibus_DAT_MOSI                       (cpu_1_ibus_DAT_MOSI[31:0]                           ), //i
    .cpu_ibus_SEL                            (cpu_1_ibus_SEL[3:0]                                 ), //i
    .cpu_ibus_ERR                            (bridge_cpu_ibus_ERR                                 ), //o
    .cpu_ibus_CTI                            (cpu_1_ibus_CTI[2:0]                                 ), //i
    .cpu_ibus_BTE                            (cpu_1_ibus_BTE[1:0]                                 ), //i
    .cpu_dbus_CYC                            (cpu_1_dbus_CYC                                      ), //i
    .cpu_dbus_STB                            (cpu_1_dbus_STB                                      ), //i
    .cpu_dbus_ACK                            (bridge_cpu_dbus_ACK                                 ), //o
    .cpu_dbus_WE                             (cpu_1_dbus_WE                                       ), //i
    .cpu_dbus_ADR                            (cpu_1_dbus_ADR[29:0]                                ), //i
    .cpu_dbus_DAT_MISO                       (bridge_cpu_dbus_DAT_MISO[31:0]                      ), //o
    .cpu_dbus_DAT_MOSI                       (cpu_1_dbus_DAT_MOSI[31:0]                           ), //i
    .cpu_dbus_SEL                            (cpu_1_dbus_SEL[3:0]                                 ), //i
    .cpu_dbus_ERR                            (bridge_cpu_dbus_ERR                                 ), //o
    .cpu_dbus_CTI                            (cpu_1_dbus_CTI[2:0]                                 ), //i
    .cpu_dbus_BTE                            (cpu_1_dbus_BTE[1:0]                                 ), //i
    .devices_ibus_CYC                        (bridge_devices_ibus_CYC                             ), //o
    .devices_ibus_STB                        (bridge_devices_ibus_STB                             ), //o
    .devices_ibus_ACK                        (wishboneBusSlaveMultiplexer_4_master_ACK            ), //i
    .devices_ibus_WE                         (bridge_devices_ibus_WE                              ), //o
    .devices_ibus_ADR                        (bridge_devices_ibus_ADR[29:0]                       ), //o
    .devices_ibus_DAT_MISO                   (wishboneBusSlaveMultiplexer_4_master_DAT_MISO[31:0] ), //i
    .devices_ibus_DAT_MOSI                   (bridge_devices_ibus_DAT_MOSI[31:0]                  ), //o
    .devices_ibus_SEL                        (bridge_devices_ibus_SEL[3:0]                        ), //o
    .devices_dataOnly_CYC                    (bridge_devices_dataOnly_CYC                         ), //o
    .devices_dataOnly_STB                    (bridge_devices_dataOnly_STB                         ), //o
    .devices_dataOnly_ACK                    (dbusOnlySlaveMux_master_ACK                         ), //i
    .devices_dataOnly_WE                     (bridge_devices_dataOnly_WE                          ), //o
    .devices_dataOnly_ADR                    (bridge_devices_dataOnly_ADR[29:0]                   ), //o
    .devices_dataOnly_DAT_MISO               (dbusOnlySlaveMux_master_DAT_MISO[31:0]              ), //i
    .devices_dataOnly_DAT_MOSI               (bridge_devices_dataOnly_DAT_MOSI[31:0]              ), //o
    .devices_dataOnly_SEL                    (bridge_devices_dataOnly_SEL[3:0]                    ), //o
    .devices_dbusToExecutableBridge_CYC      (dbusOnlySlaveMux_slaves_0_CYC                       ), //i
    .devices_dbusToExecutableBridge_STB      (dbusOnlySlaveMux_slaves_0_STB                       ), //i
    .devices_dbusToExecutableBridge_ACK      (bridge_devices_dbusToExecutableBridge_ACK           ), //o
    .devices_dbusToExecutableBridge_WE       (dbusOnlySlaveMux_slaves_0_WE                        ), //i
    .devices_dbusToExecutableBridge_ADR      (dbusOnlySlaveMux_slaves_0_ADR[29:0]                 ), //i
    .devices_dbusToExecutableBridge_DAT_MISO (bridge_devices_dbusToExecutableBridge_DAT_MISO[31:0]), //o
    .devices_dbusToExecutableBridge_DAT_MOSI (dbusOnlySlaveMux_slaves_0_DAT_MOSI[31:0]            ), //i
    .devices_dbusToExecutableBridge_SEL      (dbusOnlySlaveMux_slaves_0_SEL[3:0]                  ), //i
    .devices_executable_CYC                  (bridge_devices_executable_CYC                       ), //o
    .devices_executable_STB                  (bridge_devices_executable_STB                       ), //o
    .devices_executable_ACK                  (wishboneBusSlaveMultiplexer_3_master_ACK            ), //i
    .devices_executable_WE                   (bridge_devices_executable_WE                        ), //o
    .devices_executable_ADR                  (bridge_devices_executable_ADR[29:0]                 ), //o
    .devices_executable_DAT_MISO             (wishboneBusSlaveMultiplexer_3_master_DAT_MISO[31:0] ), //i
    .devices_executable_DAT_MOSI             (bridge_devices_executable_DAT_MOSI[31:0]            ), //o
    .devices_executable_SEL                  (bridge_devices_executable_SEL[3:0]                  )  //o
  );
  WishboneBusMasterSlaveMap_1 dbusSlaveMap (
    .masters_0_index          (dbusSlaveMap_masters_0_index         ), //o
    .masters_0_isValid        (dbusSlaveMap_masters_0_isValid       ), //o
    .masters_0_slaveSelects_0 (dbusSlaveMap_masters_0_slaveSelects_0), //i
    .masters_0_slaveSelects_1 (dbusSlaveMap_masters_0_slaveSelects_1)  //i
  );
  WishboneBusMasterSlaveMap executableSlaveMap (
    .masters_0_index          (executableSlaveMap_masters_0_index         ), //o
    .masters_0_isValid        (executableSlaveMap_masters_0_isValid       ), //o
    .masters_0_slaveSelects_0 (executableSlaveMap_masters_0_slaveSelects_0), //i
    .masters_0_slaveSelects_1 (executableSlaveMap_masters_0_slaveSelects_1), //i
    .masters_1_index          (executableSlaveMap_masters_1_index         ), //o
    .masters_1_isValid        (executableSlaveMap_masters_1_isValid       ), //o
    .masters_1_slaveSelects_0 (executableSlaveMap_masters_1_slaveSelects_0), //i
    .masters_1_slaveSelects_1 (executableSlaveMap_masters_1_slaveSelects_1)  //i
  );
  WishboneBusSlaveMultiplexer_2 dbusOnlySlaveMux (
    .master_CYC        (bridge_devices_dataOnly_CYC                         ), //i
    .master_STB        (bridge_devices_dataOnly_STB                         ), //i
    .master_ACK        (dbusOnlySlaveMux_master_ACK                         ), //o
    .master_WE         (bridge_devices_dataOnly_WE                          ), //i
    .master_ADR        (bridge_devices_dataOnly_ADR[29:0]                   ), //i
    .master_DAT_MISO   (dbusOnlySlaveMux_master_DAT_MISO[31:0]              ), //o
    .master_DAT_MOSI   (bridge_devices_dataOnly_DAT_MOSI[31:0]              ), //i
    .master_SEL        (bridge_devices_dataOnly_SEL[3:0]                    ), //i
    .slaves_0_CYC      (dbusOnlySlaveMux_slaves_0_CYC                       ), //o
    .slaves_0_STB      (dbusOnlySlaveMux_slaves_0_STB                       ), //o
    .slaves_0_ACK      (bridge_devices_dbusToExecutableBridge_ACK           ), //i
    .slaves_0_WE       (dbusOnlySlaveMux_slaves_0_WE                        ), //o
    .slaves_0_ADR      (dbusOnlySlaveMux_slaves_0_ADR[29:0]                 ), //o
    .slaves_0_DAT_MISO (bridge_devices_dbusToExecutableBridge_DAT_MISO[31:0]), //i
    .slaves_0_DAT_MOSI (dbusOnlySlaveMux_slaves_0_DAT_MOSI[31:0]            ), //o
    .slaves_0_SEL      (dbusOnlySlaveMux_slaves_0_SEL[3:0]                  ), //o
    .slaves_1_CYC      (dbusOnlySlaveMux_slaves_1_CYC                       ), //o
    .slaves_1_STB      (dbusOnlySlaveMux_slaves_1_STB                       ), //o
    .slaves_1_ACK      (ledDeviceWidthAdjusted_master_ACK                   ), //i
    .slaves_1_WE       (dbusOnlySlaveMux_slaves_1_WE                        ), //o
    .slaves_1_ADR      (dbusOnlySlaveMux_slaves_1_ADR[29:0]                 ), //o
    .slaves_1_DAT_MISO (ledDeviceWidthAdjusted_master_DAT_MISO[31:0]        ), //i
    .slaves_1_DAT_MOSI (dbusOnlySlaveMux_slaves_1_DAT_MOSI[31:0]            ), //o
    .slaves_1_SEL      (dbusOnlySlaveMux_slaves_1_SEL[3:0]                  ), //o
    .selector          (dbusSlaveMap_masters_0_index                        )  //i
  );
  PriorityEncoder_1 priorityEncoder_2 (
    .inputs_0 (sharedSlaveArbiters_0_0_io_encoder_inputs_0), //i
    .inputs_1 (sharedSlaveArbiters_0_0_io_encoder_inputs_1), //i
    .output_1 (priorityEncoder_2_output_1                 ), //o
    .isValid  (priorityEncoder_2_isValid                  )  //o
  );
  MultiMasterSingleSlaveArbiter_1 sharedSlaveArbiters_0_0 (
    .io_encoder_inputs_0    (sharedSlaveArbiters_0_0_io_encoder_inputs_0   ), //o
    .io_encoder_inputs_1    (sharedSlaveArbiters_0_0_io_encoder_inputs_1   ), //o
    .io_encoder_output      (priorityEncoder_2_output_1                    ), //i
    .io_encoder_isValid     (priorityEncoder_2_isValid                     ), //i
    .io_grantedMasterIndex  (sharedSlaveArbiters_0_0_io_grantedMasterIndex ), //o
    .io_masters_0_request   (sharedSlaveArbiters_0_0_io_masters_0_request  ), //i
    .io_masters_0_isError   (sharedSlaveArbiters_0_0_io_masters_0_isError  ), //o
    .io_masters_0_isStalled (sharedSlaveArbiters_0_0_io_masters_0_isStalled), //o
    .io_masters_0_isGranted (sharedSlaveArbiters_0_0_io_masters_0_isGranted), //o
    .io_masters_1_request   (sharedSlaveArbiters_0_0_io_masters_1_request  ), //i
    .io_masters_1_isError   (sharedSlaveArbiters_0_0_io_masters_1_isError  ), //o
    .io_masters_1_isStalled (sharedSlaveArbiters_0_0_io_masters_1_isStalled), //o
    .io_masters_1_isGranted (sharedSlaveArbiters_0_0_io_masters_1_isGranted), //o
    .clk                    (clk                                           ), //i
    .reset                  (reset                                         )  //i
  );
  PriorityEncoder_1 priorityEncoder_3 (
    .inputs_0 (sharedSlaveArbiters_1_0_io_encoder_inputs_0), //i
    .inputs_1 (sharedSlaveArbiters_1_0_io_encoder_inputs_1), //i
    .output_1 (priorityEncoder_3_output_1                 ), //o
    .isValid  (priorityEncoder_3_isValid                  )  //o
  );
  MultiMasterSingleSlaveArbiter_1 sharedSlaveArbiters_1_0 (
    .io_encoder_inputs_0    (sharedSlaveArbiters_1_0_io_encoder_inputs_0   ), //o
    .io_encoder_inputs_1    (sharedSlaveArbiters_1_0_io_encoder_inputs_1   ), //o
    .io_encoder_output      (priorityEncoder_3_output_1                    ), //i
    .io_encoder_isValid     (priorityEncoder_3_isValid                     ), //i
    .io_grantedMasterIndex  (sharedSlaveArbiters_1_0_io_grantedMasterIndex ), //o
    .io_masters_0_request   (sharedSlaveArbiters_1_0_io_masters_0_request  ), //i
    .io_masters_0_isError   (sharedSlaveArbiters_1_0_io_masters_0_isError  ), //o
    .io_masters_0_isStalled (sharedSlaveArbiters_1_0_io_masters_0_isStalled), //o
    .io_masters_0_isGranted (sharedSlaveArbiters_1_0_io_masters_0_isGranted), //o
    .io_masters_1_request   (sharedSlaveArbiters_1_0_io_masters_1_request  ), //i
    .io_masters_1_isError   (sharedSlaveArbiters_1_0_io_masters_1_isError  ), //o
    .io_masters_1_isStalled (sharedSlaveArbiters_1_0_io_masters_1_isStalled), //o
    .io_masters_1_isGranted (sharedSlaveArbiters_1_0_io_masters_1_isGranted), //o
    .clk                    (clk                                           ), //i
    .reset                  (reset                                         )  //i
  );
  WishboneBusMasterMultiplexer_1 masterMuxes_0_0 (
    .masters_0_CYC      (masterMuxes_0_1_0_CYC                                   ), //i
    .masters_0_STB      (masterMuxes_0_1_0_STB                                   ), //i
    .masters_0_ACK      (masterMuxes_0_0_masters_0_ACK                           ), //o
    .masters_0_WE       (masterMuxes_0_1_0_WE                                    ), //i
    .masters_0_ADR      (masterMuxes_0_1_0_ADR[29:0]                             ), //i
    .masters_0_DAT_MISO (masterMuxes_0_0_masters_0_DAT_MISO[31:0]                ), //o
    .masters_0_DAT_MOSI (masterMuxes_0_1_0_DAT_MOSI[31:0]                        ), //i
    .masters_0_SEL      (masterMuxes_0_1_0_SEL[3:0]                              ), //i
    .masters_1_CYC      (masterMuxes_0_1_1_CYC                                   ), //i
    .masters_1_STB      (masterMuxes_0_1_1_STB                                   ), //i
    .masters_1_ACK      (masterMuxes_0_0_masters_1_ACK                           ), //o
    .masters_1_WE       (masterMuxes_0_1_1_WE                                    ), //i
    .masters_1_ADR      (masterMuxes_0_1_1_ADR[29:0]                             ), //i
    .masters_1_DAT_MISO (masterMuxes_0_0_masters_1_DAT_MISO[31:0]                ), //o
    .masters_1_DAT_MOSI (masterMuxes_0_1_1_DAT_MOSI[31:0]                        ), //i
    .masters_1_SEL      (masterMuxes_0_1_1_SEL[3:0]                              ), //i
    .slave_CYC          (masterMuxes_0_0_slave_CYC                               ), //o
    .slave_STB          (masterMuxes_0_0_slave_STB                               ), //o
    .slave_ACK          (instructionEbramBlockWidthAdjusted_master_ACK           ), //i
    .slave_WE           (masterMuxes_0_0_slave_WE                                ), //o
    .slave_ADR          (masterMuxes_0_0_slave_ADR[29:0]                         ), //o
    .slave_DAT_MISO     (instructionEbramBlockWidthAdjusted_master_DAT_MISO[31:0]), //i
    .slave_DAT_MOSI     (masterMuxes_0_0_slave_DAT_MOSI[31:0]                    ), //o
    .slave_SEL          (masterMuxes_0_0_slave_SEL[3:0]                          ), //o
    .selector           (sharedSlaveArbiters_0_0_io_grantedMasterIndex           )  //i
  );
  WishboneBusMasterMultiplexer_1 masterMuxes_1_0 (
    .masters_0_CYC      (masterMuxes_1_1_0_CYC                            ), //i
    .masters_0_STB      (masterMuxes_1_1_0_STB                            ), //i
    .masters_0_ACK      (masterMuxes_1_0_masters_0_ACK                    ), //o
    .masters_0_WE       (masterMuxes_1_1_0_WE                             ), //i
    .masters_0_ADR      (masterMuxes_1_1_0_ADR[29:0]                      ), //i
    .masters_0_DAT_MISO (masterMuxes_1_0_masters_0_DAT_MISO[31:0]         ), //o
    .masters_0_DAT_MOSI (masterMuxes_1_1_0_DAT_MOSI[31:0]                 ), //i
    .masters_0_SEL      (masterMuxes_1_1_0_SEL[3:0]                       ), //i
    .masters_1_CYC      (masterMuxes_1_1_1_CYC                            ), //i
    .masters_1_STB      (masterMuxes_1_1_1_STB                            ), //i
    .masters_1_ACK      (masterMuxes_1_0_masters_1_ACK                    ), //o
    .masters_1_WE       (masterMuxes_1_1_1_WE                             ), //i
    .masters_1_ADR      (masterMuxes_1_1_1_ADR[29:0]                      ), //i
    .masters_1_DAT_MISO (masterMuxes_1_0_masters_1_DAT_MISO[31:0]         ), //o
    .masters_1_DAT_MOSI (masterMuxes_1_1_1_DAT_MOSI[31:0]                 ), //i
    .masters_1_SEL      (masterMuxes_1_1_1_SEL[3:0]                       ), //i
    .slave_CYC          (masterMuxes_1_0_slave_CYC                        ), //o
    .slave_STB          (masterMuxes_1_0_slave_STB                        ), //o
    .slave_ACK          (dataSpramBlockWidthAdjusted_master_ACK           ), //i
    .slave_WE           (masterMuxes_1_0_slave_WE                         ), //o
    .slave_ADR          (masterMuxes_1_0_slave_ADR[29:0]                  ), //o
    .slave_DAT_MISO     (dataSpramBlockWidthAdjusted_master_DAT_MISO[31:0]), //i
    .slave_DAT_MOSI     (masterMuxes_1_0_slave_DAT_MOSI[31:0]             ), //o
    .slave_SEL          (masterMuxes_1_0_slave_SEL[3:0]                   ), //o
    .selector           (sharedSlaveArbiters_1_0_io_grantedMasterIndex    )  //i
  );
  WishboneBusSlaveMultiplexer_2 wishboneBusSlaveMultiplexer_3 (
    .master_CYC        (bridge_devices_executable_CYC                        ), //i
    .master_STB        (bridge_devices_executable_STB                        ), //i
    .master_ACK        (wishboneBusSlaveMultiplexer_3_master_ACK             ), //o
    .master_WE         (bridge_devices_executable_WE                         ), //i
    .master_ADR        (bridge_devices_executable_ADR[29:0]                  ), //i
    .master_DAT_MISO   (wishboneBusSlaveMultiplexer_3_master_DAT_MISO[31:0]  ), //o
    .master_DAT_MOSI   (bridge_devices_executable_DAT_MOSI[31:0]             ), //i
    .master_SEL        (bridge_devices_executable_SEL[3:0]                   ), //i
    .slaves_0_CYC      (wishboneBusSlaveMultiplexer_3_slaves_0_CYC           ), //o
    .slaves_0_STB      (wishboneBusSlaveMultiplexer_3_slaves_0_STB           ), //o
    .slaves_0_ACK      (masterMuxes_0_1_0_ACK                                ), //i
    .slaves_0_WE       (wishboneBusSlaveMultiplexer_3_slaves_0_WE            ), //o
    .slaves_0_ADR      (wishboneBusSlaveMultiplexer_3_slaves_0_ADR[29:0]     ), //o
    .slaves_0_DAT_MISO (masterMuxes_0_1_0_DAT_MISO[31:0]                     ), //i
    .slaves_0_DAT_MOSI (wishboneBusSlaveMultiplexer_3_slaves_0_DAT_MOSI[31:0]), //o
    .slaves_0_SEL      (wishboneBusSlaveMultiplexer_3_slaves_0_SEL[3:0]      ), //o
    .slaves_1_CYC      (wishboneBusSlaveMultiplexer_3_slaves_1_CYC           ), //o
    .slaves_1_STB      (wishboneBusSlaveMultiplexer_3_slaves_1_STB           ), //o
    .slaves_1_ACK      (masterMuxes_1_1_0_ACK                                ), //i
    .slaves_1_WE       (wishboneBusSlaveMultiplexer_3_slaves_1_WE            ), //o
    .slaves_1_ADR      (wishboneBusSlaveMultiplexer_3_slaves_1_ADR[29:0]     ), //o
    .slaves_1_DAT_MISO (masterMuxes_1_1_0_DAT_MISO[31:0]                     ), //i
    .slaves_1_DAT_MOSI (wishboneBusSlaveMultiplexer_3_slaves_1_DAT_MOSI[31:0]), //o
    .slaves_1_SEL      (wishboneBusSlaveMultiplexer_3_slaves_1_SEL[3:0]      ), //o
    .selector          (executableSlaveMap_masters_0_index                   )  //i
  );
  WishboneBusSlaveMultiplexer_2 wishboneBusSlaveMultiplexer_4 (
    .master_CYC        (bridge_devices_ibus_CYC                              ), //i
    .master_STB        (bridge_devices_ibus_STB                              ), //i
    .master_ACK        (wishboneBusSlaveMultiplexer_4_master_ACK             ), //o
    .master_WE         (bridge_devices_ibus_WE                               ), //i
    .master_ADR        (bridge_devices_ibus_ADR[29:0]                        ), //i
    .master_DAT_MISO   (wishboneBusSlaveMultiplexer_4_master_DAT_MISO[31:0]  ), //o
    .master_DAT_MOSI   (bridge_devices_ibus_DAT_MOSI[31:0]                   ), //i
    .master_SEL        (bridge_devices_ibus_SEL[3:0]                         ), //i
    .slaves_0_CYC      (wishboneBusSlaveMultiplexer_4_slaves_0_CYC           ), //o
    .slaves_0_STB      (wishboneBusSlaveMultiplexer_4_slaves_0_STB           ), //o
    .slaves_0_ACK      (masterMuxes_0_1_1_ACK                                ), //i
    .slaves_0_WE       (wishboneBusSlaveMultiplexer_4_slaves_0_WE            ), //o
    .slaves_0_ADR      (wishboneBusSlaveMultiplexer_4_slaves_0_ADR[29:0]     ), //o
    .slaves_0_DAT_MISO (masterMuxes_0_1_1_DAT_MISO[31:0]                     ), //i
    .slaves_0_DAT_MOSI (wishboneBusSlaveMultiplexer_4_slaves_0_DAT_MOSI[31:0]), //o
    .slaves_0_SEL      (wishboneBusSlaveMultiplexer_4_slaves_0_SEL[3:0]      ), //o
    .slaves_1_CYC      (wishboneBusSlaveMultiplexer_4_slaves_1_CYC           ), //o
    .slaves_1_STB      (wishboneBusSlaveMultiplexer_4_slaves_1_STB           ), //o
    .slaves_1_ACK      (masterMuxes_1_1_1_ACK                                ), //i
    .slaves_1_WE       (wishboneBusSlaveMultiplexer_4_slaves_1_WE            ), //o
    .slaves_1_ADR      (wishboneBusSlaveMultiplexer_4_slaves_1_ADR[29:0]     ), //o
    .slaves_1_DAT_MISO (masterMuxes_1_1_1_DAT_MISO[31:0]                     ), //i
    .slaves_1_DAT_MOSI (wishboneBusSlaveMultiplexer_4_slaves_1_DAT_MOSI[31:0]), //o
    .slaves_1_SEL      (wishboneBusSlaveMultiplexer_4_slaves_1_SEL[3:0]      ), //o
    .selector          (executableSlaveMap_masters_1_index                   )  //i
  );
  assign p23 = ledDevice_io_p23; // @[Core.scala 55:16]
  assign ledR = ledDevice_io_ledR; // @[Core.scala 56:17]
  assign ledG = ledDevice_io_ledG; // @[Core.scala 57:17]
  assign ledB = ledDevice_io_ledB; // @[Core.scala 58:17]
  assign dbusSlaveMap_masters_0_slaveSelects_0 = (! bridge_devices_dataOnly_ADR[14]); // @[WishboneBusMasterSlaveMap.scala 98:90]
  assign dbusSlaveMap_masters_0_slaveSelects_1 = bridge_devices_dataOnly_ADR[14]; // @[WishboneBusMasterSlaveMap.scala 98:90]
  assign executableSlaveMap_masters_0_slaveSelects_0 = ((! bridge_devices_executable_ADR[14]) && (! bridge_devices_executable_ADR[6])); // @[WishboneBusMasterSlaveMap.scala 98:90]
  assign executableSlaveMap_masters_1_slaveSelects_0 = ((! bridge_devices_ibus_ADR[14]) && (! bridge_devices_ibus_ADR[6])); // @[WishboneBusMasterSlaveMap.scala 98:90]
  assign executableSlaveMap_masters_0_slaveSelects_1 = ((! bridge_devices_executable_ADR[14]) && bridge_devices_executable_ADR[6]); // @[WishboneBusMasterSlaveMap.scala 98:90]
  assign executableSlaveMap_masters_1_slaveSelects_1 = ((! bridge_devices_ibus_ADR[14]) && bridge_devices_ibus_ADR[6]); // @[WishboneBusMasterSlaveMap.scala 98:90]
  assign sharedSlaveArbiters_0_0_io_masters_0_request = (bridge_devices_executable_CYC && (executableSlaveMap_masters_1_index == 1'b0)); // @[Core.scala 215:76]
  assign sharedSlaveArbiters_0_0_io_masters_1_request = (bridge_devices_ibus_CYC && (executableSlaveMap_masters_1_index == 1'b0)); // @[Core.scala 215:76]
  assign sharedSlaveArbiters_1_0_io_masters_0_request = (bridge_devices_executable_CYC && (executableSlaveMap_masters_1_index == 1'b1)); // @[Core.scala 215:76]
  assign sharedSlaveArbiters_1_0_io_masters_1_request = (bridge_devices_ibus_CYC && (executableSlaveMap_masters_1_index == 1'b1)); // @[Core.scala 215:76]
  assign masterMuxes_0_1_0_ACK = masterMuxes_0_0_masters_0_ACK; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_0_1_0_DAT_MISO = masterMuxes_0_0_masters_0_DAT_MISO; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_0_1_1_ACK = masterMuxes_0_0_masters_1_ACK; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_0_1_1_DAT_MISO = masterMuxes_0_0_masters_1_DAT_MISO; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_1_1_0_ACK = masterMuxes_1_0_masters_0_ACK; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_1_1_0_DAT_MISO = masterMuxes_1_0_masters_0_DAT_MISO; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_1_1_1_ACK = masterMuxes_1_0_masters_1_ACK; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_1_1_1_DAT_MISO = masterMuxes_1_0_masters_1_DAT_MISO; // @[WishboneBusMasterMultiplexer.scala 80:112]
  assign masterMuxes_0_1_0_CYC = wishboneBusSlaveMultiplexer_3_slaves_0_CYC; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_0_STB = wishboneBusSlaveMultiplexer_3_slaves_0_STB; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_0_WE = wishboneBusSlaveMultiplexer_3_slaves_0_WE; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_0_ADR = wishboneBusSlaveMultiplexer_3_slaves_0_ADR; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_0_DAT_MOSI = wishboneBusSlaveMultiplexer_3_slaves_0_DAT_MOSI; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_0_SEL = wishboneBusSlaveMultiplexer_3_slaves_0_SEL; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_0_CYC = wishboneBusSlaveMultiplexer_3_slaves_1_CYC; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_0_STB = wishboneBusSlaveMultiplexer_3_slaves_1_STB; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_0_WE = wishboneBusSlaveMultiplexer_3_slaves_1_WE; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_0_ADR = wishboneBusSlaveMultiplexer_3_slaves_1_ADR; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_0_DAT_MOSI = wishboneBusSlaveMultiplexer_3_slaves_1_DAT_MOSI; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_0_SEL = wishboneBusSlaveMultiplexer_3_slaves_1_SEL; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_1_CYC = wishboneBusSlaveMultiplexer_4_slaves_0_CYC; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_1_STB = wishboneBusSlaveMultiplexer_4_slaves_0_STB; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_1_WE = wishboneBusSlaveMultiplexer_4_slaves_0_WE; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_1_ADR = wishboneBusSlaveMultiplexer_4_slaves_0_ADR; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_1_DAT_MOSI = wishboneBusSlaveMultiplexer_4_slaves_0_DAT_MOSI; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_0_1_1_SEL = wishboneBusSlaveMultiplexer_4_slaves_0_SEL; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_1_CYC = wishboneBusSlaveMultiplexer_4_slaves_1_CYC; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_1_STB = wishboneBusSlaveMultiplexer_4_slaves_1_STB; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_1_WE = wishboneBusSlaveMultiplexer_4_slaves_1_WE; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_1_ADR = wishboneBusSlaveMultiplexer_4_slaves_1_ADR; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_1_DAT_MOSI = wishboneBusSlaveMultiplexer_4_slaves_1_DAT_MOSI; // @[WishboneBusSlaveMultiplexer.scala 84:106]
  assign masterMuxes_1_1_1_SEL = wishboneBusSlaveMultiplexer_4_slaves_1_SEL; // @[WishboneBusSlaveMultiplexer.scala 84:106]

endmodule

//WishboneBusSlaveMultiplexer_2 replaced by WishboneBusSlaveMultiplexer_2

//WishboneBusSlaveMultiplexer_2 replaced by WishboneBusSlaveMultiplexer_2

//WishboneBusMasterMultiplexer_1 replaced by WishboneBusMasterMultiplexer_1

module WishboneBusMasterMultiplexer_1 (
  input               masters_0_CYC,
  input               masters_0_STB,
  output              masters_0_ACK,
  input               masters_0_WE,
  input      [29:0]   masters_0_ADR,
  output     [31:0]   masters_0_DAT_MISO,
  input      [31:0]   masters_0_DAT_MOSI,
  input      [3:0]    masters_0_SEL,
  input               masters_1_CYC,
  input               masters_1_STB,
  output              masters_1_ACK,
  input               masters_1_WE,
  input      [29:0]   masters_1_ADR,
  output     [31:0]   masters_1_DAT_MISO,
  input      [31:0]   masters_1_DAT_MOSI,
  input      [3:0]    masters_1_SEL,
  output reg          slave_CYC,
  output reg          slave_STB,
  input               slave_ACK,
  output reg          slave_WE,
  output reg [29:0]   slave_ADR,
  input      [31:0]   slave_DAT_MISO,
  output reg [31:0]   slave_DAT_MOSI,
  output reg [3:0]    slave_SEL,
  input      [0:0]    selector
);


  assign masters_0_DAT_MISO = slave_DAT_MISO; // @[WishboneBusMasterMultiplexer.scala 14:33]
  assign masters_0_ACK = (slave_ACK && (selector == 1'b0)); // @[WishboneBusMasterMultiplexer.scala 15:28]
  assign masters_1_DAT_MISO = slave_DAT_MISO; // @[WishboneBusMasterMultiplexer.scala 14:33]
  assign masters_1_ACK = (slave_ACK && (selector == 1'b1)); // @[WishboneBusMasterMultiplexer.scala 15:28]
  always @(*) begin
    case(selector)
      1'b0 : begin
        slave_DAT_MOSI = masters_0_DAT_MOSI; // @[WishboneBusMasterMultiplexer.scala 24:51]
      end
      default : begin
        slave_DAT_MOSI = masters_1_DAT_MOSI; // @[WishboneBusMasterMultiplexer.scala 24:51]
      end
    endcase
  end

  always @(*) begin
    case(selector)
      1'b0 : begin
        slave_ADR = masters_0_ADR; // @[WishboneBusMasterMultiplexer.scala 25:46]
      end
      default : begin
        slave_ADR = masters_1_ADR; // @[WishboneBusMasterMultiplexer.scala 25:46]
      end
    endcase
  end

  always @(*) begin
    case(selector)
      1'b0 : begin
        slave_WE = masters_0_WE; // @[WishboneBusMasterMultiplexer.scala 26:45]
      end
      default : begin
        slave_WE = masters_1_WE; // @[WishboneBusMasterMultiplexer.scala 26:45]
      end
    endcase
  end

  always @(*) begin
    case(selector)
      1'b0 : begin
        slave_CYC = masters_0_CYC; // @[WishboneBusMasterMultiplexer.scala 27:46]
      end
      default : begin
        slave_CYC = masters_1_CYC; // @[WishboneBusMasterMultiplexer.scala 27:46]
      end
    endcase
  end

  always @(*) begin
    case(selector)
      1'b0 : begin
        slave_STB = masters_0_STB; // @[WishboneBusMasterMultiplexer.scala 28:46]
      end
      default : begin
        slave_STB = masters_1_STB; // @[WishboneBusMasterMultiplexer.scala 28:46]
      end
    endcase
  end

  always @(*) begin
    case(selector)
      1'b0 : begin
        slave_SEL = masters_0_SEL; // @[WishboneBusMasterMultiplexer.scala 29:60]
      end
      default : begin
        slave_SEL = masters_1_SEL; // @[WishboneBusMasterMultiplexer.scala 29:60]
      end
    endcase
  end


endmodule

//MultiMasterSingleSlaveArbiter_1 replaced by MultiMasterSingleSlaveArbiter_1

//PriorityEncoder_1 replaced by PriorityEncoder_1

module MultiMasterSingleSlaveArbiter_1 (
  output              io_encoder_inputs_0,
  output              io_encoder_inputs_1,
  input      [0:0]    io_encoder_output,
  input               io_encoder_isValid,
  output     [0:0]    io_grantedMasterIndex,
  input               io_masters_0_request,
  output              io_masters_0_isError,
  output              io_masters_0_isStalled,
  output              io_masters_0_isGranted,
  input               io_masters_1_request,
  output              io_masters_1_isError,
  output              io_masters_1_isStalled,
  output              io_masters_1_isGranted,
  input               clk,
  input               reset
);

  reg        [0:0]    grantedMasterIndex;
  wire                when_MultiMasterSingleSlaveArbiter_l18;
  wire                when_MultiMasterSingleSlaveArbiter_l18_1;

  assign io_grantedMasterIndex = grantedMasterIndex; // @[MultiMasterSingleSlaveArbiter.scala 13:31]
  assign when_MultiMasterSingleSlaveArbiter_l18 = (io_encoder_isValid && (! io_masters_0_request)); // @[BaseType.scala 305:24]
  assign when_MultiMasterSingleSlaveArbiter_l18_1 = (io_encoder_isValid && (! io_masters_1_request)); // @[BaseType.scala 305:24]
  assign io_encoder_inputs_0 = io_masters_0_request; // @[MultiMasterSingleSlaveArbiter.scala 26:50]
  assign io_masters_0_isError = (io_masters_0_request && (! io_encoder_isValid)); // @[MultiMasterSingleSlaveArbiter.scala 27:40]
  assign io_masters_0_isStalled = (io_masters_0_request && (grantedMasterIndex != 1'b0)); // @[MultiMasterSingleSlaveArbiter.scala 28:42]
  assign io_masters_0_isGranted = (io_masters_0_request && (grantedMasterIndex == 1'b0)); // @[MultiMasterSingleSlaveArbiter.scala 29:42]
  assign io_encoder_inputs_1 = io_masters_1_request; // @[MultiMasterSingleSlaveArbiter.scala 26:50]
  assign io_masters_1_isError = (io_masters_1_request && (! io_encoder_isValid)); // @[MultiMasterSingleSlaveArbiter.scala 27:40]
  assign io_masters_1_isStalled = (io_masters_1_request && (grantedMasterIndex != 1'b1)); // @[MultiMasterSingleSlaveArbiter.scala 28:42]
  assign io_masters_1_isGranted = (io_masters_1_request && (grantedMasterIndex == 1'b1)); // @[MultiMasterSingleSlaveArbiter.scala 29:42]
  always @(posedge clk) begin
    if(reset) begin
      grantedMasterIndex <= 1'b0; // @[Data.scala 400:33]
    end else begin
      case(grantedMasterIndex)
        1'b0 : begin
          if(when_MultiMasterSingleSlaveArbiter_l18) begin
            grantedMasterIndex <= io_encoder_output; // @[MultiMasterSingleSlaveArbiter.scala 19:68]
          end
        end
        default : begin
          if(when_MultiMasterSingleSlaveArbiter_l18_1) begin
            grantedMasterIndex <= io_encoder_output; // @[MultiMasterSingleSlaveArbiter.scala 19:68]
          end
        end
      endcase
    end
  end


endmodule

module PriorityEncoder_1 (
  input               inputs_0,
  input               inputs_1,
  output reg [0:0]    output_1,
  output reg          isValid
);

  wire       [1:0]    _zz_switch_PriorityEncoder_l14;
  wire       [1:0]    switch_PriorityEncoder_l14;

  assign _zz_switch_PriorityEncoder_l14 = {inputs_1,inputs_0};
  assign switch_PriorityEncoder_l14 = _zz_switch_PriorityEncoder_l14[1 : 0]; // @[BaseType.scala 299:24]
  always @(*) begin
    casez(switch_PriorityEncoder_l14)
      2'b?1 : begin
        output_1 = 1'b0; // @[PriorityEncoder.scala 17:43]
      end
      2'b10 : begin
        output_1 = 1'b1; // @[PriorityEncoder.scala 17:43]
      end
      default : begin
        output_1 = 1'b0; // @[PriorityEncoder.scala 23:35]
      end
    endcase
  end

  always @(*) begin
    casez(switch_PriorityEncoder_l14)
      2'b?1 : begin
        isValid = 1'b1; // @[PriorityEncoder.scala 18:44]
      end
      2'b10 : begin
        isValid = 1'b1; // @[PriorityEncoder.scala 18:44]
      end
      default : begin
        isValid = 1'b0; // @[PriorityEncoder.scala 24:36]
      end
    endcase
  end


endmodule

module WishboneBusSlaveMultiplexer_2 (
  input               master_CYC,
  input               master_STB,
  output reg          master_ACK,
  input               master_WE,
  input      [29:0]   master_ADR,
  output reg [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [3:0]    master_SEL,
  output              slaves_0_CYC,
  output              slaves_0_STB,
  input               slaves_0_ACK,
  output              slaves_0_WE,
  output     [29:0]   slaves_0_ADR,
  input      [31:0]   slaves_0_DAT_MISO,
  output     [31:0]   slaves_0_DAT_MOSI,
  output     [3:0]    slaves_0_SEL,
  output              slaves_1_CYC,
  output              slaves_1_STB,
  input               slaves_1_ACK,
  output              slaves_1_WE,
  output     [29:0]   slaves_1_ADR,
  input      [31:0]   slaves_1_DAT_MISO,
  output     [31:0]   slaves_1_DAT_MOSI,
  output     [3:0]    slaves_1_SEL,
  input      [0:0]    selector
);


  assign slaves_0_DAT_MOSI = master_DAT_MOSI; // @[WishboneBusSlaveMultiplexer.scala 14:32]
  assign slaves_0_ADR = master_ADR; // @[WishboneBusSlaveMultiplexer.scala 15:27]
  assign slaves_0_WE = master_WE; // @[WishboneBusSlaveMultiplexer.scala 16:26]
  assign slaves_0_CYC = (master_CYC && (selector == 1'b0)); // @[WishboneBusSlaveMultiplexer.scala 17:27]
  assign slaves_0_STB = (master_STB && (selector == 1'b0)); // @[WishboneBusSlaveMultiplexer.scala 18:27]
  assign slaves_0_SEL = master_SEL; // @[WishboneBusSlaveMultiplexer.scala 19:41]
  assign slaves_1_DAT_MOSI = master_DAT_MOSI; // @[WishboneBusSlaveMultiplexer.scala 14:32]
  assign slaves_1_ADR = master_ADR; // @[WishboneBusSlaveMultiplexer.scala 15:27]
  assign slaves_1_WE = master_WE; // @[WishboneBusSlaveMultiplexer.scala 16:26]
  assign slaves_1_CYC = (master_CYC && (selector == 1'b1)); // @[WishboneBusSlaveMultiplexer.scala 17:27]
  assign slaves_1_STB = (master_STB && (selector == 1'b1)); // @[WishboneBusSlaveMultiplexer.scala 18:27]
  assign slaves_1_SEL = master_SEL; // @[WishboneBusSlaveMultiplexer.scala 19:41]
  always @(*) begin
    case(selector)
      1'b0 : begin
        master_DAT_MISO = slaves_0_DAT_MISO; // @[WishboneBusSlaveMultiplexer.scala 25:52]
      end
      default : begin
        master_DAT_MISO = slaves_1_DAT_MISO; // @[WishboneBusSlaveMultiplexer.scala 25:52]
      end
    endcase
  end

  always @(*) begin
    case(selector)
      1'b0 : begin
        master_ACK = slaves_0_ACK; // @[WishboneBusSlaveMultiplexer.scala 26:47]
      end
      default : begin
        master_ACK = slaves_1_ACK; // @[WishboneBusSlaveMultiplexer.scala 26:47]
      end
    endcase
  end


endmodule

module WishboneBusMasterSlaveMap (
  output     [0:0]    masters_0_index,
  output              masters_0_isValid,
  input               masters_0_slaveSelects_0,
  input               masters_0_slaveSelects_1,
  output     [0:0]    masters_1_index,
  output              masters_1_isValid,
  input               masters_1_slaveSelects_0,
  input               masters_1_slaveSelects_1
);

  wire       [0:0]    simpleEncoder_3_output_1;
  wire                simpleEncoder_3_isValid;
  wire       [0:0]    simpleEncoder_4_output_1;
  wire                simpleEncoder_4_isValid;

  SimpleEncoder_2 simpleEncoder_3 (
    .inputs_0 (masters_0_slaveSelects_0), //i
    .inputs_1 (masters_0_slaveSelects_1), //i
    .output_1 (simpleEncoder_3_output_1), //o
    .isValid  (simpleEncoder_3_isValid )  //o
  );
  SimpleEncoder_2 simpleEncoder_4 (
    .inputs_0 (masters_1_slaveSelects_0), //i
    .inputs_1 (masters_1_slaveSelects_1), //i
    .output_1 (simpleEncoder_4_output_1), //o
    .isValid  (simpleEncoder_4_isValid )  //o
  );
  assign masters_0_index = simpleEncoder_3_output_1; // @[WishboneBusMasterSlaveMap.scala 48:30]
  assign masters_0_isValid = simpleEncoder_3_isValid; // @[WishboneBusMasterSlaveMap.scala 49:32]
  assign masters_1_index = simpleEncoder_4_output_1; // @[WishboneBusMasterSlaveMap.scala 48:30]
  assign masters_1_isValid = simpleEncoder_4_isValid; // @[WishboneBusMasterSlaveMap.scala 49:32]

endmodule

module WishboneBusMasterSlaveMap_1 (
  output     [0:0]    masters_0_index,
  output              masters_0_isValid,
  input               masters_0_slaveSelects_0,
  input               masters_0_slaveSelects_1
);

  wire       [0:0]    simpleEncoder_3_output_1;
  wire                simpleEncoder_3_isValid;

  SimpleEncoder_2 simpleEncoder_3 (
    .inputs_0 (masters_0_slaveSelects_0), //i
    .inputs_1 (masters_0_slaveSelects_1), //i
    .output_1 (simpleEncoder_3_output_1), //o
    .isValid  (simpleEncoder_3_isValid )  //o
  );
  assign masters_0_index = simpleEncoder_3_output_1; // @[WishboneBusMasterSlaveMap.scala 48:30]
  assign masters_0_isValid = simpleEncoder_3_isValid; // @[WishboneBusMasterSlaveMap.scala 49:32]

endmodule

module CpuBusBridge (
  input               cpu_ibus_CYC,
  input               cpu_ibus_STB,
  output              cpu_ibus_ACK,
  input               cpu_ibus_WE,
  input      [29:0]   cpu_ibus_ADR,
  output     [31:0]   cpu_ibus_DAT_MISO,
  input      [31:0]   cpu_ibus_DAT_MOSI,
  input      [3:0]    cpu_ibus_SEL,
  output              cpu_ibus_ERR,
  input      [2:0]    cpu_ibus_CTI,
  input      [1:0]    cpu_ibus_BTE,
  input               cpu_dbus_CYC,
  input               cpu_dbus_STB,
  output              cpu_dbus_ACK,
  input               cpu_dbus_WE,
  input      [29:0]   cpu_dbus_ADR,
  output     [31:0]   cpu_dbus_DAT_MISO,
  input      [31:0]   cpu_dbus_DAT_MOSI,
  input      [3:0]    cpu_dbus_SEL,
  output              cpu_dbus_ERR,
  input      [2:0]    cpu_dbus_CTI,
  input      [1:0]    cpu_dbus_BTE,
  output              devices_ibus_CYC,
  output              devices_ibus_STB,
  input               devices_ibus_ACK,
  output              devices_ibus_WE,
  output     [29:0]   devices_ibus_ADR,
  input      [31:0]   devices_ibus_DAT_MISO,
  output     [31:0]   devices_ibus_DAT_MOSI,
  output     [3:0]    devices_ibus_SEL,
  output              devices_dataOnly_CYC,
  output              devices_dataOnly_STB,
  input               devices_dataOnly_ACK,
  output              devices_dataOnly_WE,
  output     [29:0]   devices_dataOnly_ADR,
  input      [31:0]   devices_dataOnly_DAT_MISO,
  output     [31:0]   devices_dataOnly_DAT_MOSI,
  output     [3:0]    devices_dataOnly_SEL,
  input               devices_dbusToExecutableBridge_CYC,
  input               devices_dbusToExecutableBridge_STB,
  output              devices_dbusToExecutableBridge_ACK,
  input               devices_dbusToExecutableBridge_WE,
  input      [29:0]   devices_dbusToExecutableBridge_ADR,
  output     [31:0]   devices_dbusToExecutableBridge_DAT_MISO,
  input      [31:0]   devices_dbusToExecutableBridge_DAT_MOSI,
  input      [3:0]    devices_dbusToExecutableBridge_SEL,
  output              devices_executable_CYC,
  output              devices_executable_STB,
  input               devices_executable_ACK,
  output              devices_executable_WE,
  output     [29:0]   devices_executable_ADR,
  input      [31:0]   devices_executable_DAT_MISO,
  output     [31:0]   devices_executable_DAT_MOSI,
  output     [3:0]    devices_executable_SEL
);

  wire       [31:0]   cpu_ibus_adapter_io_wbm_DAT_MISO;
  wire                cpu_ibus_adapter_io_wbm_ACK;
  wire                cpu_ibus_adapter_io_wbm_ERR;
  wire       [31:0]   cpu_ibus_adapter_io_wbs_DAT_MOSI;
  wire       [29:0]   cpu_ibus_adapter_io_wbs_ADR;
  wire                cpu_ibus_adapter_io_wbs_CYC;
  wire       [3:0]    cpu_ibus_adapter_io_wbs_SEL;
  wire                cpu_ibus_adapter_io_wbs_STB;
  wire                cpu_ibus_adapter_io_wbs_WE;
  wire       [31:0]   cpu_dbus_adapter_io_wbm_DAT_MISO;
  wire                cpu_dbus_adapter_io_wbm_ACK;
  wire                cpu_dbus_adapter_io_wbm_ERR;
  wire       [31:0]   cpu_dbus_adapter_io_wbs_DAT_MOSI;
  wire       [29:0]   cpu_dbus_adapter_io_wbs_ADR;
  wire                cpu_dbus_adapter_io_wbs_CYC;
  wire       [3:0]    cpu_dbus_adapter_io_wbs_SEL;
  wire                cpu_dbus_adapter_io_wbs_STB;
  wire                cpu_dbus_adapter_io_wbs_WE;

  WishboneAdapter_1 cpu_ibus_adapter (
    .io_wbm_CYC      (cpu_ibus_CYC                          ), //i
    .io_wbm_STB      (cpu_ibus_STB                          ), //i
    .io_wbm_ACK      (cpu_ibus_adapter_io_wbm_ACK           ), //o
    .io_wbm_WE       (cpu_ibus_WE                           ), //i
    .io_wbm_ADR      (cpu_ibus_ADR[29:0]                    ), //i
    .io_wbm_DAT_MISO (cpu_ibus_adapter_io_wbm_DAT_MISO[31:0]), //o
    .io_wbm_DAT_MOSI (cpu_ibus_DAT_MOSI[31:0]               ), //i
    .io_wbm_SEL      (cpu_ibus_SEL[3:0]                     ), //i
    .io_wbm_ERR      (cpu_ibus_adapter_io_wbm_ERR           ), //o
    .io_wbm_CTI      (cpu_ibus_CTI[2:0]                     ), //i
    .io_wbm_BTE      (cpu_ibus_BTE[1:0]                     ), //i
    .io_wbs_CYC      (cpu_ibus_adapter_io_wbs_CYC           ), //o
    .io_wbs_STB      (cpu_ibus_adapter_io_wbs_STB           ), //o
    .io_wbs_ACK      (devices_ibus_ACK                      ), //i
    .io_wbs_WE       (cpu_ibus_adapter_io_wbs_WE            ), //o
    .io_wbs_ADR      (cpu_ibus_adapter_io_wbs_ADR[29:0]     ), //o
    .io_wbs_DAT_MISO (devices_ibus_DAT_MISO[31:0]           ), //i
    .io_wbs_DAT_MOSI (cpu_ibus_adapter_io_wbs_DAT_MOSI[31:0]), //o
    .io_wbs_SEL      (cpu_ibus_adapter_io_wbs_SEL[3:0]      )  //o
  );
  WishboneAdapter_1 cpu_dbus_adapter (
    .io_wbm_CYC      (cpu_dbus_CYC                          ), //i
    .io_wbm_STB      (cpu_dbus_STB                          ), //i
    .io_wbm_ACK      (cpu_dbus_adapter_io_wbm_ACK           ), //o
    .io_wbm_WE       (cpu_dbus_WE                           ), //i
    .io_wbm_ADR      (cpu_dbus_ADR[29:0]                    ), //i
    .io_wbm_DAT_MISO (cpu_dbus_adapter_io_wbm_DAT_MISO[31:0]), //o
    .io_wbm_DAT_MOSI (cpu_dbus_DAT_MOSI[31:0]               ), //i
    .io_wbm_SEL      (cpu_dbus_SEL[3:0]                     ), //i
    .io_wbm_ERR      (cpu_dbus_adapter_io_wbm_ERR           ), //o
    .io_wbm_CTI      (cpu_dbus_CTI[2:0]                     ), //i
    .io_wbm_BTE      (cpu_dbus_BTE[1:0]                     ), //i
    .io_wbs_CYC      (cpu_dbus_adapter_io_wbs_CYC           ), //o
    .io_wbs_STB      (cpu_dbus_adapter_io_wbs_STB           ), //o
    .io_wbs_ACK      (devices_dataOnly_ACK                  ), //i
    .io_wbs_WE       (cpu_dbus_adapter_io_wbs_WE            ), //o
    .io_wbs_ADR      (cpu_dbus_adapter_io_wbs_ADR[29:0]     ), //o
    .io_wbs_DAT_MISO (devices_dataOnly_DAT_MISO[31:0]       ), //i
    .io_wbs_DAT_MOSI (cpu_dbus_adapter_io_wbs_DAT_MOSI[31:0]), //o
    .io_wbs_SEL      (cpu_dbus_adapter_io_wbs_SEL[3:0]      )  //o
  );
  assign cpu_ibus_ACK = cpu_ibus_adapter_io_wbm_ACK; // @[WishboneAdapter.scala 33:12]
  assign cpu_ibus_DAT_MISO = cpu_ibus_adapter_io_wbm_DAT_MISO; // @[WishboneAdapter.scala 33:12]
  assign cpu_ibus_ERR = cpu_ibus_adapter_io_wbm_ERR; // @[WishboneAdapter.scala 33:12]
  assign devices_ibus_CYC = cpu_ibus_adapter_io_wbs_CYC; // @[WishboneAdapter.scala 34:11]
  assign devices_ibus_STB = cpu_ibus_adapter_io_wbs_STB; // @[WishboneAdapter.scala 34:11]
  assign devices_ibus_WE = cpu_ibus_adapter_io_wbs_WE; // @[WishboneAdapter.scala 34:11]
  assign devices_ibus_ADR = cpu_ibus_adapter_io_wbs_ADR; // @[WishboneAdapter.scala 34:11]
  assign devices_ibus_DAT_MOSI = cpu_ibus_adapter_io_wbs_DAT_MOSI; // @[WishboneAdapter.scala 34:11]
  assign devices_ibus_SEL = cpu_ibus_adapter_io_wbs_SEL; // @[WishboneAdapter.scala 34:11]
  assign cpu_dbus_ACK = cpu_dbus_adapter_io_wbm_ACK; // @[WishboneAdapter.scala 33:12]
  assign cpu_dbus_DAT_MISO = cpu_dbus_adapter_io_wbm_DAT_MISO; // @[WishboneAdapter.scala 33:12]
  assign cpu_dbus_ERR = cpu_dbus_adapter_io_wbm_ERR; // @[WishboneAdapter.scala 33:12]
  assign devices_dataOnly_CYC = cpu_dbus_adapter_io_wbs_CYC; // @[WishboneAdapter.scala 34:11]
  assign devices_dataOnly_STB = cpu_dbus_adapter_io_wbs_STB; // @[WishboneAdapter.scala 34:11]
  assign devices_dataOnly_WE = cpu_dbus_adapter_io_wbs_WE; // @[WishboneAdapter.scala 34:11]
  assign devices_dataOnly_ADR = cpu_dbus_adapter_io_wbs_ADR; // @[WishboneAdapter.scala 34:11]
  assign devices_dataOnly_DAT_MOSI = cpu_dbus_adapter_io_wbs_DAT_MOSI; // @[WishboneAdapter.scala 34:11]
  assign devices_dataOnly_SEL = cpu_dbus_adapter_io_wbs_SEL; // @[WishboneAdapter.scala 34:11]
  assign devices_executable_CYC = devices_dbusToExecutableBridge_CYC; // @[Wishbone.scala 152:19]
  assign devices_executable_ADR = devices_dbusToExecutableBridge_ADR; // @[Wishbone.scala 153:19]
  assign devices_executable_DAT_MOSI = devices_dbusToExecutableBridge_DAT_MOSI; // @[Wishbone.scala 154:19]
  assign devices_dbusToExecutableBridge_DAT_MISO = devices_executable_DAT_MISO; // @[Wishbone.scala 155:19]
  assign devices_executable_STB = devices_dbusToExecutableBridge_STB; // @[Wishbone.scala 156:19]
  assign devices_executable_WE = devices_dbusToExecutableBridge_WE; // @[Wishbone.scala 157:19]
  assign devices_dbusToExecutableBridge_ACK = devices_executable_ACK; // @[Wishbone.scala 158:19]
  assign devices_executable_SEL = devices_dbusToExecutableBridge_SEL; // @[Wishbone.scala 257:33]

endmodule

module WishboneBusAddressMappingAdapter (
  input               master_CYC,
  input               master_STB,
  output              master_ACK,
  input               master_WE,
  input      [29:0]   master_ADR,
  output     [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [3:0]    master_SEL,
  output              slave_CYC,
  output              slave_STB,
  input               slave_ACK,
  output              slave_WE,
  output     [0:0]    slave_ADR,
  input      [31:0]   slave_DAT_MISO,
  output     [31:0]   slave_DAT_MOSI,
  output     [3:0]    slave_SEL
);


  assign slave_ADR = master_ADR[0:0]; // @[WishboneBusAddressMappingAdapter.scala 20:30]
  assign master_DAT_MISO = slave_DAT_MISO; // @[WishboneBusAddressMappingAdapter.scala 25:28]
  assign master_ACK = slave_ACK; // @[WishboneBusAddressMappingAdapter.scala 26:23]
  assign slave_CYC = master_CYC; // @[WishboneBusAddressMappingAdapter.scala 31:22]
  assign slave_WE = master_WE; // @[WishboneBusAddressMappingAdapter.scala 32:21]
  assign slave_STB = master_STB; // @[WishboneBusAddressMappingAdapter.scala 33:22]
  assign slave_DAT_MOSI = master_DAT_MOSI; // @[WishboneBusAddressMappingAdapter.scala 34:27]
  assign slave_SEL = master_SEL; // @[WishboneBusAddressMappingAdapter.scala 35:36]

endmodule

module WishboneBusAddressMappingAdapter_1 (
  input               master_CYC,
  input               master_STB,
  output              master_ACK,
  input               master_WE,
  input      [29:0]   master_ADR,
  output     [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [3:0]    master_SEL,
  output              slave_CYC,
  output              slave_STB,
  input               slave_ACK,
  output              slave_WE,
  output     [13:0]   slave_ADR,
  input      [31:0]   slave_DAT_MISO,
  output     [31:0]   slave_DAT_MOSI,
  output     [3:0]    slave_SEL
);


  assign slave_ADR = master_ADR[13:0]; // @[WishboneBusAddressMappingAdapter.scala 20:30]
  assign master_DAT_MISO = slave_DAT_MISO; // @[WishboneBusAddressMappingAdapter.scala 25:28]
  assign master_ACK = slave_ACK; // @[WishboneBusAddressMappingAdapter.scala 26:23]
  assign slave_CYC = master_CYC; // @[WishboneBusAddressMappingAdapter.scala 31:22]
  assign slave_WE = master_WE; // @[WishboneBusAddressMappingAdapter.scala 32:21]
  assign slave_STB = master_STB; // @[WishboneBusAddressMappingAdapter.scala 33:22]
  assign slave_DAT_MOSI = master_DAT_MOSI; // @[WishboneBusAddressMappingAdapter.scala 34:27]
  assign slave_SEL = master_SEL; // @[WishboneBusAddressMappingAdapter.scala 35:36]

endmodule

module WishboneBusSelMappingAdapter (
  input               master_CYC,
  input               master_STB,
  output              master_ACK,
  input               master_WE,
  input      [13:0]   master_ADR,
  output     [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [3:0]    master_SEL,
  output              slave_CYC,
  output              slave_STB,
  input               slave_ACK,
  output              slave_WE,
  output     [13:0]   slave_ADR,
  input      [31:0]   slave_DAT_MISO,
  output     [31:0]   slave_DAT_MOSI,
  output     [7:0]    slave_SEL
);

  wire                _zz_slave_SEL;
  wire                _zz_slave_SEL_1;
  wire                _zz_slave_SEL_2;
  wire                _zz_slave_SEL_3;
  reg        [7:0]    _zz_slave_SEL_4;

  assign _zz_slave_SEL = master_SEL[3]; // @[BaseType.scala 305:24]
  assign _zz_slave_SEL_1 = master_SEL[2]; // @[BaseType.scala 305:24]
  assign _zz_slave_SEL_2 = master_SEL[1]; // @[BaseType.scala 305:24]
  assign _zz_slave_SEL_3 = master_SEL[0]; // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_slave_SEL_4[7] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[6] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[5] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[4] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[3] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[2] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[1] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[0] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
  end

  assign slave_SEL = _zz_slave_SEL_4; // @[WishboneBusSelMappingAdapter.scala 20:30]
  assign master_DAT_MISO = slave_DAT_MISO; // @[WishboneBusSelMappingAdapter.scala 23:28]
  assign master_ACK = slave_ACK; // @[WishboneBusSelMappingAdapter.scala 24:23]
  assign slave_CYC = master_CYC; // @[WishboneBusSelMappingAdapter.scala 29:22]
  assign slave_WE = master_WE; // @[WishboneBusSelMappingAdapter.scala 30:21]
  assign slave_ADR = master_ADR; // @[WishboneBusSelMappingAdapter.scala 31:22]
  assign slave_STB = master_STB; // @[WishboneBusSelMappingAdapter.scala 32:22]
  assign slave_DAT_MOSI = master_DAT_MOSI; // @[WishboneBusSelMappingAdapter.scala 33:27]

endmodule

module WishboneBusDataExpander (
  input               master_CYC,
  input               master_STB,
  output              master_ACK,
  input               master_WE,
  input      [13:0]   master_ADR,
  output reg [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [7:0]    master_SEL,
  output              slaves_0_CYC,
  output              slaves_0_STB,
  input               slaves_0_ACK,
  output              slaves_0_WE,
  output     [13:0]   slaves_0_ADR,
  input      [15:0]   slaves_0_DAT_MISO,
  output     [15:0]   slaves_0_DAT_MOSI,
  output     [3:0]    slaves_0_SEL,
  output              slaves_1_CYC,
  output              slaves_1_STB,
  input               slaves_1_ACK,
  output              slaves_1_WE,
  output     [13:0]   slaves_1_ADR,
  input      [15:0]   slaves_1_DAT_MISO,
  output     [15:0]   slaves_1_DAT_MOSI,
  output     [3:0]    slaves_1_SEL
);


  assign master_ACK = ((1'b1 && slaves_0_ACK) && slaves_1_ACK); // @[WishboneBusDataExpander.scala 12:23]
  assign slaves_0_ADR = master_ADR; // @[WishboneBusDataExpander.scala 20:27]
  assign slaves_0_CYC = master_CYC; // @[WishboneBusDataExpander.scala 21:27]
  assign slaves_0_STB = master_STB; // @[WishboneBusDataExpander.scala 22:27]
  assign slaves_0_SEL = master_SEL[3 : 0]; // @[WishboneBusDataExpander.scala 23:41]
  assign slaves_0_WE = master_WE; // @[WishboneBusDataExpander.scala 24:26]
  assign slaves_0_DAT_MOSI = master_DAT_MOSI[15 : 0]; // @[WishboneBusDataExpander.scala 25:32]
  always @(*) begin
    master_DAT_MISO[15 : 0] = slaves_0_DAT_MISO; // @[WishboneBusDataExpander.scala 26:48]
    master_DAT_MISO[31 : 16] = slaves_1_DAT_MISO; // @[WishboneBusDataExpander.scala 26:48]
  end

  assign slaves_1_ADR = master_ADR; // @[WishboneBusDataExpander.scala 20:27]
  assign slaves_1_CYC = master_CYC; // @[WishboneBusDataExpander.scala 21:27]
  assign slaves_1_STB = master_STB; // @[WishboneBusDataExpander.scala 22:27]
  assign slaves_1_SEL = master_SEL[7 : 4]; // @[WishboneBusDataExpander.scala 23:41]
  assign slaves_1_WE = master_WE; // @[WishboneBusDataExpander.scala 24:26]
  assign slaves_1_DAT_MOSI = master_DAT_MOSI[31 : 16]; // @[WishboneBusDataExpander.scala 25:32]

endmodule

//Ice40Spram16k16WishboneBusAdapter_1 replaced by Ice40Spram16k16WishboneBusAdapter_1

module Ice40Spram16k16WishboneBusAdapter_1 (
  input               wishbone_CYC,
  input               wishbone_STB,
  output              wishbone_ACK,
  input               wishbone_WE,
  input      [13:0]   wishbone_ADR,
  output     [15:0]   wishbone_DAT_MISO,
  input      [15:0]   wishbone_DAT_MOSI,
  input      [3:0]    wishbone_SEL,
  output     [13:0]   spram_AD,
  output     [15:0]   spram_DI,
  output     [3:0]    spram_MASKWE,
  output              spram_WE,
  output              spram_CS,
  input      [15:0]   spram_DO,
  input               clk,
  input               reset
);

  reg                 wishbone_STB_regNext;

  assign wishbone_ACK = (wishbone_STB_regNext && wishbone_CYC); // @[Ice40Spram16k16WishboneBusAdapter.scala 11:25]
  assign wishbone_DAT_MISO = spram_DO; // @[Ice40Spram16k16WishboneBusAdapter.scala 12:30]
  assign spram_AD = wishbone_ADR; // @[Ice40Spram16k16WishboneBusAdapter.scala 14:21]
  assign spram_DI = wishbone_DAT_MOSI; // @[Ice40Spram16k16WishboneBusAdapter.scala 15:21]
  assign spram_MASKWE = wishbone_SEL; // @[Ice40Spram16k16WishboneBusAdapter.scala 16:25]
  assign spram_WE = (wishbone_STB && wishbone_WE); // @[Ice40Spram16k16WishboneBusAdapter.scala 17:21]
  assign spram_CS = wishbone_CYC; // @[Ice40Spram16k16WishboneBusAdapter.scala 18:21]
  always @(posedge clk) begin
    if(reset) begin
      wishbone_STB_regNext <= 1'b0; // @[Data.scala 400:33]
    end else begin
      wishbone_STB_regNext <= wishbone_STB; // @[Reg.scala 39:30]
    end
  end


endmodule

module WishboneBusAddressMappingAdapter_2 (
  input               master_CYC,
  input               master_STB,
  output              master_ACK,
  input               master_WE,
  input      [29:0]   master_ADR,
  output     [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [3:0]    master_SEL,
  output              slave_CYC,
  output              slave_STB,
  input               slave_ACK,
  output              slave_WE,
  output     [7:0]    slave_ADR,
  input      [31:0]   slave_DAT_MISO,
  output     [31:0]   slave_DAT_MOSI,
  output     [3:0]    slave_SEL
);


  assign slave_ADR = master_ADR[7:0]; // @[WishboneBusAddressMappingAdapter.scala 20:30]
  assign master_DAT_MISO = slave_DAT_MISO; // @[WishboneBusAddressMappingAdapter.scala 25:28]
  assign master_ACK = slave_ACK; // @[WishboneBusAddressMappingAdapter.scala 26:23]
  assign slave_CYC = master_CYC; // @[WishboneBusAddressMappingAdapter.scala 31:22]
  assign slave_WE = master_WE; // @[WishboneBusAddressMappingAdapter.scala 32:21]
  assign slave_STB = master_STB; // @[WishboneBusAddressMappingAdapter.scala 33:22]
  assign slave_DAT_MOSI = master_DAT_MOSI; // @[WishboneBusAddressMappingAdapter.scala 34:27]
  assign slave_SEL = master_SEL; // @[WishboneBusAddressMappingAdapter.scala 35:36]

endmodule

module WishboneBusSelMappingAdapter_1 (
  input               master_CYC,
  input               master_STB,
  output              master_ACK,
  input               master_WE,
  input      [7:0]    master_ADR,
  output     [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [3:0]    master_SEL,
  output              slave_CYC,
  output              slave_STB,
  input               slave_ACK,
  output              slave_WE,
  output     [7:0]    slave_ADR,
  input      [31:0]   slave_DAT_MISO,
  output     [31:0]   slave_DAT_MOSI,
  output     [31:0]   slave_SEL
);

  wire                _zz_slave_SEL;
  wire                _zz_slave_SEL_1;
  wire                _zz_slave_SEL_2;
  wire                _zz_slave_SEL_3;
  reg        [31:0]   _zz_slave_SEL_4;

  assign _zz_slave_SEL = master_SEL[3]; // @[BaseType.scala 305:24]
  assign _zz_slave_SEL_1 = master_SEL[2]; // @[BaseType.scala 305:24]
  assign _zz_slave_SEL_2 = master_SEL[1]; // @[BaseType.scala 305:24]
  assign _zz_slave_SEL_3 = master_SEL[0]; // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_slave_SEL_4[31] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[30] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[29] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[28] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[27] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[26] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[25] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[24] = _zz_slave_SEL; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[23] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[22] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[21] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[20] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[19] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[18] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[17] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[16] = _zz_slave_SEL_1; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[15] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[14] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[13] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[12] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[11] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[10] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[9] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[8] = _zz_slave_SEL_2; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[7] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[6] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[5] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[4] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[3] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[2] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[1] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
    _zz_slave_SEL_4[0] = _zz_slave_SEL_3; // @[Literal.scala 87:17]
  end

  assign slave_SEL = _zz_slave_SEL_4; // @[WishboneBusSelMappingAdapter.scala 20:30]
  assign master_DAT_MISO = slave_DAT_MISO; // @[WishboneBusSelMappingAdapter.scala 23:28]
  assign master_ACK = slave_ACK; // @[WishboneBusSelMappingAdapter.scala 24:23]
  assign slave_CYC = master_CYC; // @[WishboneBusSelMappingAdapter.scala 29:22]
  assign slave_WE = master_WE; // @[WishboneBusSelMappingAdapter.scala 30:21]
  assign slave_ADR = master_ADR; // @[WishboneBusSelMappingAdapter.scala 31:22]
  assign slave_STB = master_STB; // @[WishboneBusSelMappingAdapter.scala 32:22]
  assign slave_DAT_MOSI = master_DAT_MOSI; // @[WishboneBusSelMappingAdapter.scala 33:27]

endmodule

module WishboneBusDataExpander_1 (
  input               master_CYC,
  input               master_STB,
  output              master_ACK,
  input               master_WE,
  input      [7:0]    master_ADR,
  output reg [31:0]   master_DAT_MISO,
  input      [31:0]   master_DAT_MOSI,
  input      [31:0]   master_SEL,
  output              slaves_0_CYC,
  output              slaves_0_STB,
  input               slaves_0_ACK,
  output              slaves_0_WE,
  output     [7:0]    slaves_0_ADR,
  input      [15:0]   slaves_0_DAT_MISO,
  output     [15:0]   slaves_0_DAT_MOSI,
  output     [15:0]   slaves_0_SEL,
  output              slaves_1_CYC,
  output              slaves_1_STB,
  input               slaves_1_ACK,
  output              slaves_1_WE,
  output     [7:0]    slaves_1_ADR,
  input      [15:0]   slaves_1_DAT_MISO,
  output     [15:0]   slaves_1_DAT_MOSI,
  output     [15:0]   slaves_1_SEL
);


  assign master_ACK = ((1'b1 && slaves_0_ACK) && slaves_1_ACK); // @[WishboneBusDataExpander.scala 12:23]
  assign slaves_0_ADR = master_ADR; // @[WishboneBusDataExpander.scala 20:27]
  assign slaves_0_CYC = master_CYC; // @[WishboneBusDataExpander.scala 21:27]
  assign slaves_0_STB = master_STB; // @[WishboneBusDataExpander.scala 22:27]
  assign slaves_0_SEL = master_SEL[15 : 0]; // @[WishboneBusDataExpander.scala 23:41]
  assign slaves_0_WE = master_WE; // @[WishboneBusDataExpander.scala 24:26]
  assign slaves_0_DAT_MOSI = master_DAT_MOSI[15 : 0]; // @[WishboneBusDataExpander.scala 25:32]
  always @(*) begin
    master_DAT_MISO[15 : 0] = slaves_0_DAT_MISO; // @[WishboneBusDataExpander.scala 26:48]
    master_DAT_MISO[31 : 16] = slaves_1_DAT_MISO; // @[WishboneBusDataExpander.scala 26:48]
  end

  assign slaves_1_ADR = master_ADR; // @[WishboneBusDataExpander.scala 20:27]
  assign slaves_1_CYC = master_CYC; // @[WishboneBusDataExpander.scala 21:27]
  assign slaves_1_STB = master_STB; // @[WishboneBusDataExpander.scala 22:27]
  assign slaves_1_SEL = master_SEL[31 : 16]; // @[WishboneBusDataExpander.scala 23:41]
  assign slaves_1_WE = master_WE; // @[WishboneBusDataExpander.scala 24:26]
  assign slaves_1_DAT_MOSI = master_DAT_MOSI[31 : 16]; // @[WishboneBusDataExpander.scala 25:32]

endmodule

//Ice40Ebram4k16WishboneBusAdapter_1 replaced by Ice40Ebram4k16WishboneBusAdapter_1

module Ice40Ebram4k (
  input      [15:0]   DI,
  input      [7:0]    ADW,
  input      [7:0]    ADR,
  input               CKW,
  input               CKR,
  input               CEW,
  input               CER,
  input               RE,
  input               WE,
  input      [15:0]   MASK_N,
  output     [15:0]   DO_1
);

  wire       [10:0]   native_ADW;
  wire       [10:0]   native_ADR;
  wire       [15:0]   native_DO;

  PDP4K #(
    .DATA_WIDTH_R("16"),
    .DATA_WIDTH_W("16"),
    .INITVAL_0("0x00C40A14000D0099A374000300999B7400010099913400140099493400401000"),
    .INITVAL_1("0xAB34000A0299023400990099493400100099463401240099493400140099A1B4"),
    .INITVAL_2("0x029906F400C45FF4000F0299EE3400D4055400070299A93400C4FA9400100299"),
    .INITVAL_3("0x0000000000000000000000000000000000000000000000000000000000001000"),
    .INITVAL_4("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_5("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_6("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_7("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_8("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_9("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_A("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_B("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_C("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_D("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_E("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_F("0x0000000000000000000000000000000000000000000000000000000000000000")
  ) native (
    .DI     (DI[15:0]        ), //i
    .ADW    (native_ADW[10:0]), //i
    .ADR    (native_ADR[10:0]), //i
    .CKW    (CKW             ), //i
    .CKR    (CKR             ), //i
    .CEW    (CEW             ), //i
    .CER    (CER             ), //i
    .RE     (RE              ), //i
    .WE     (WE              ), //i
    .MASK_N (MASK_N[15:0]    ), //i
    .DO     (native_DO[15:0] )  //o
  );
  assign native_ADW = {3'd0, ADW}; // @[Ice40Ebram4k.scala 17:23]
  assign native_ADR = {3'd0, ADR}; // @[Ice40Ebram4k.scala 18:23]
  assign DO_1 = native_DO; // @[Ice40Ebram4k.scala 69:23]

endmodule

module Ice40Ebram4k16WishboneBusAdapter_1 (
  input               wishbone_CYC,
  input               wishbone_STB,
  output              wishbone_ACK,
  input               wishbone_WE,
  input      [7:0]    wishbone_ADR,
  output     [15:0]   wishbone_DAT_MISO,
  input      [15:0]   wishbone_DAT_MOSI,
  input      [15:0]   wishbone_SEL,
  output     [15:0]   ebram_DI,
  output     [7:0]    ebram_ADW,
  output     [7:0]    ebram_ADR,
  output              ebram_CEW,
  output              ebram_CER,
  output              ebram_RE,
  output              ebram_WE,
  output     [15:0]   ebram_MASK_N,
  input      [15:0]   ebram_DO,
  input               clk,
  input               reset
);

  reg                 isNotReadWaitState;

  assign wishbone_ACK = (wishbone_CYC && ((wishbone_WE && wishbone_STB) || isNotReadWaitState)); // @[Ice40Ebram4k16WishboneBusAdapter .scala 12:25]
  assign wishbone_DAT_MISO = ebram_DO; // @[Ice40Ebram4k16WishboneBusAdapter .scala 13:30]
  assign ebram_ADR = wishbone_ADR; // @[Ice40Ebram4k16WishboneBusAdapter .scala 14:22]
  assign ebram_ADW = wishbone_ADR; // @[Ice40Ebram4k16WishboneBusAdapter .scala 15:22]
  assign ebram_DI = wishbone_DAT_MOSI; // @[Ice40Ebram4k16WishboneBusAdapter .scala 17:21]
  assign ebram_MASK_N = (~ wishbone_SEL); // @[Ice40Ebram4k16WishboneBusAdapter .scala 19:25]
  assign ebram_RE = wishbone_STB; // @[Ice40Ebram4k16WishboneBusAdapter .scala 20:21]
  assign ebram_WE = (wishbone_STB && wishbone_WE); // @[Ice40Ebram4k16WishboneBusAdapter .scala 21:21]
  assign ebram_CER = (wishbone_CYC && (! wishbone_WE)); // @[Ice40Ebram4k16WishboneBusAdapter .scala 22:22]
  assign ebram_CEW = (wishbone_CYC && wishbone_WE); // @[Ice40Ebram4k16WishboneBusAdapter .scala 23:22]
  always @(posedge clk) begin
    if(reset) begin
      isNotReadWaitState <= 1'b0; // @[Data.scala 400:33]
    end else begin
      isNotReadWaitState <= ((! wishbone_WE) && wishbone_STB); // @[Reg.scala 39:30]
    end
  end


endmodule

module Ice40Ebram4k_1 (
  input      [15:0]   DI,
  input      [7:0]    ADW,
  input      [7:0]    ADR,
  input               CKW,
  input               CKR,
  input               CEW,
  input               CER,
  input               RE,
  input               WE,
  input      [15:0]   MASK_N,
  output     [15:0]   DO_1
);

  wire       [10:0]   native_ADW;
  wire       [10:0]   native_ADR;
  wire       [15:0]   native_DO;

  PDP4K #(
    .DATA_WIDTH_R("16"),
    .DATA_WIDTH_W("16"),
    .INITVAL_0("0x9493849344B72623849324B72423849314B722238493A4B72023849304B70913"),
    .INITVAL_1("0x849314B720238493A4B72E23849304B72C23849394B72A23849394B728238493"),
    .INITVAL_2("0x2823849394938493E4B72623849394938493F4B7242384939493849304B72223"),
    .INITVAL_3("0x00000000000000000000000000000000000000000000000000000000000000E7"),
    .INITVAL_4("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_5("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_6("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_7("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_8("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_9("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_A("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_B("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_C("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_D("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_E("0x0000000000000000000000000000000000000000000000000000000000000000"),
    .INITVAL_F("0x0000000000000000000000000000000000000000000000000000000000000000")
  ) native (
    .DI     (DI[15:0]        ), //i
    .ADW    (native_ADW[10:0]), //i
    .ADR    (native_ADR[10:0]), //i
    .CKW    (CKW             ), //i
    .CKR    (CKR             ), //i
    .CEW    (CEW             ), //i
    .CER    (CER             ), //i
    .RE     (RE              ), //i
    .WE     (WE              ), //i
    .MASK_N (MASK_N[15:0]    ), //i
    .DO     (native_DO[15:0] )  //o
  );
  assign native_ADW = {3'd0, ADW}; // @[Ice40Ebram4k.scala 17:23]
  assign native_ADR = {3'd0, ADR}; // @[Ice40Ebram4k.scala 18:23]
  assign DO_1 = native_DO; // @[Ice40Ebram4k.scala 69:23]

endmodule

module unamed (
  input               io_wishbone_CYC,
  input               io_wishbone_STB,
  output reg          io_wishbone_ACK,
  input               io_wishbone_WE,
  input      [0:0]    io_wishbone_ADR,
  output     [31:0]   io_wishbone_DAT_MISO,
  input      [31:0]   io_wishbone_DAT_MOSI,
  input      [3:0]    io_wishbone_SEL,
  output              io_p23,
  output              io_ledR,
  output              io_ledG,
  output              io_ledB,
  input               clk,
  input               reset
);

  reg                 regP23;
  reg                 regLedR;
  reg                 regLedG;
  reg                 regLedB;
  wire                when_Core_l44;

  assign io_p23 = regP23; // @[Core.scala 31:24]
  assign io_ledR = regLedR; // @[Core.scala 34:25]
  assign io_ledG = regLedG; // @[Core.scala 37:25]
  assign io_ledB = regLedB; // @[Core.scala 40:25]
  assign io_wishbone_DAT_MISO = 32'h0; // @[Core.scala 42:38]
  assign when_Core_l44 = ((io_wishbone_CYC && io_wishbone_WE) && io_wishbone_STB); // @[BaseType.scala 305:24]
  always @(*) begin
    if(when_Core_l44) begin
      io_wishbone_ACK = 1'b1; // @[Core.scala 45:41]
    end else begin
      io_wishbone_ACK = 1'b0; // @[Core.scala 51:41]
    end
  end

  always @(posedge clk) begin
    if(reset) begin
      regP23 <= 1'b0; // @[Data.scala 400:33]
      regLedR <= 1'b0; // @[Data.scala 400:33]
      regLedG <= 1'b0; // @[Data.scala 400:33]
      regLedB <= 1'b0; // @[Data.scala 400:33]
    end else begin
      if(when_Core_l44) begin
        regP23 <= (! regP23); // @[Core.scala 46:32]
        regLedR <= (! io_wishbone_DAT_MOSI[0]); // @[Core.scala 47:33]
        regLedG <= (! io_wishbone_DAT_MOSI[1]); // @[Core.scala 48:33]
        regLedB <= (! io_wishbone_DAT_MOSI[2]); // @[Core.scala 49:33]
      end
    end
  end


endmodule

module Cpu (
  output              ibus_CYC,
  output              ibus_STB,
  input               ibus_ACK,
  output              ibus_WE,
  output     [29:0]   ibus_ADR,
  input      [31:0]   ibus_DAT_MISO,
  output     [31:0]   ibus_DAT_MOSI,
  output     [3:0]    ibus_SEL,
  input               ibus_ERR,
  output     [2:0]    ibus_CTI,
  output     [1:0]    ibus_BTE,
  output              dbus_CYC,
  output              dbus_STB,
  input               dbus_ACK,
  output              dbus_WE,
  output     [29:0]   dbus_ADR,
  input      [31:0]   dbus_DAT_MISO,
  output     [31:0]   dbus_DAT_MOSI,
  output     [3:0]    dbus_SEL,
  input               dbus_ERR,
  output     [2:0]    dbus_CTI,
  output     [1:0]    dbus_BTE,
  input               interrupts_external,
  input               interrupts_timer,
  input               clk,
  input               reset
);

  reg                 cpu_1_iBus_cmd_ready;
  wire                cpu_1_iBus_rsp_valid;
  wire                cpu_1_dBus_cmd_ready;
  wire                cpu_1_dBus_rsp_ready;
  wire                cpu_1_iBus_cmd_valid;
  wire       [31:0]   cpu_1_iBus_cmd_payload_pc;
  wire                cpu_1_dBus_cmd_valid;
  wire                cpu_1_dBus_cmd_payload_wr;
  wire       [31:0]   cpu_1_dBus_cmd_payload_address;
  wire       [31:0]   cpu_1_dBus_cmd_payload_data;
  wire       [1:0]    cpu_1_dBus_cmd_payload_size;
  wire                _zz_ibus_CYC;
  wire                _zz_cpu_cpu_iBus_cmd_m2sPipe_ready;
  wire                cpu_1_cpu_1_iBus_cmd_m2sPipe_valid;
  wire                cpu_1_cpu_1_iBus_cmd_m2sPipe_ready;
  wire       [31:0]   cpu_1_cpu_1_iBus_cmd_m2sPipe_payload_pc;
  reg                 cpu_1_cpu_1_iBus_cmd_rValid;
  reg        [31:0]   cpu_1_cpu_1_iBus_cmd_rData_pc;
  wire                when_Stream_l368;
  wire                _zz_cpu_cpu_dBus_cmd_halfPipe_ready;
  wire                _zz_dbus_WE;
  reg        [3:0]    _zz_dbus_SEL;
  wire                cpu_1_cpu_1_dBus_cmd_halfPipe_valid;
  wire                cpu_1_cpu_1_dBus_cmd_halfPipe_ready;
  wire                cpu_1_cpu_1_dBus_cmd_halfPipe_payload_wr;
  wire       [31:0]   cpu_1_cpu_1_dBus_cmd_halfPipe_payload_address;
  wire       [31:0]   cpu_1_cpu_1_dBus_cmd_halfPipe_payload_data;
  wire       [1:0]    cpu_1_cpu_1_dBus_cmd_halfPipe_payload_size;
  reg                 cpu_1_cpu_1_dBus_cmd_rValid;
  wire                cpu_1_cpu_1_dBus_cmd_halfPipe_fire;
  reg                 cpu_1_cpu_1_dBus_cmd_rData_wr;
  reg        [31:0]   cpu_1_cpu_1_dBus_cmd_rData_address;
  reg        [31:0]   cpu_1_cpu_1_dBus_cmd_rData_data;
  reg        [1:0]    cpu_1_cpu_1_dBus_cmd_rData_size;
  reg        [3:0]    _zz_dbus_SEL_1;
  wire                when_DBusSimplePlugin_l189;

  VexRiscv cpu_1 (
    .iBus_cmd_valid           (cpu_1_iBus_cmd_valid                ), //o
    .iBus_cmd_ready           (cpu_1_iBus_cmd_ready                ), //i
    .iBus_cmd_payload_pc      (cpu_1_iBus_cmd_payload_pc[31:0]     ), //o
    .iBus_rsp_valid           (cpu_1_iBus_rsp_valid                ), //i
    .iBus_rsp_payload_error   (1'b0                                ), //i
    .iBus_rsp_payload_inst    (ibus_DAT_MISO[31:0]                 ), //i
    .timerInterrupt           (1'b0                                ), //i
    .externalInterrupt        (1'b0                                ), //i
    .softwareInterrupt        (1'b0                                ), //i
    .dBus_cmd_valid           (cpu_1_dBus_cmd_valid                ), //o
    .dBus_cmd_ready           (cpu_1_dBus_cmd_ready                ), //i
    .dBus_cmd_payload_wr      (cpu_1_dBus_cmd_payload_wr           ), //o
    .dBus_cmd_payload_address (cpu_1_dBus_cmd_payload_address[31:0]), //o
    .dBus_cmd_payload_data    (cpu_1_dBus_cmd_payload_data[31:0]   ), //o
    .dBus_cmd_payload_size    (cpu_1_dBus_cmd_payload_size[1:0]    ), //o
    .dBus_rsp_ready           (cpu_1_dBus_rsp_ready                ), //i
    .dBus_rsp_error           (1'b0                                ), //i
    .dBus_rsp_data            (dbus_DAT_MISO[31:0]                 ), //i
    .clk                      (clk                                 ), //i
    .reset                    (reset                               )  //i
  );
  always @(*) begin
    cpu_1_iBus_cmd_ready = cpu_1_cpu_1_iBus_cmd_m2sPipe_ready; // @[Stream.scala 367:16]
    if(when_Stream_l368) begin
      cpu_1_iBus_cmd_ready = 1'b1; // @[Stream.scala 368:35]
    end
  end

  assign when_Stream_l368 = (! cpu_1_cpu_1_iBus_cmd_m2sPipe_valid); // @[BaseType.scala 299:24]
  assign cpu_1_cpu_1_iBus_cmd_m2sPipe_valid = cpu_1_cpu_1_iBus_cmd_rValid; // @[Stream.scala 370:19]
  assign cpu_1_cpu_1_iBus_cmd_m2sPipe_payload_pc = cpu_1_cpu_1_iBus_cmd_rData_pc; // @[Stream.scala 371:21]
  assign _zz_ibus_CYC = cpu_1_cpu_1_iBus_cmd_m2sPipe_valid; // @[IBusSimplePlugin.scala 155:13]
  assign cpu_1_cpu_1_iBus_cmd_m2sPipe_ready = (cpu_1_cpu_1_iBus_cmd_m2sPipe_valid && _zz_cpu_cpu_iBus_cmd_m2sPipe_ready); // @[IBusSimplePlugin.scala 159:19]
  assign cpu_1_iBus_rsp_valid = (_zz_ibus_CYC && _zz_cpu_cpu_iBus_cmd_m2sPipe_ready); // @[IBusSimplePlugin.scala 160:15]
  assign ibus_CYC = _zz_ibus_CYC; // @[Cpu.scala 102:17]
  assign ibus_STB = cpu_1_cpu_1_iBus_cmd_m2sPipe_valid; // @[Cpu.scala 102:17]
  assign _zz_cpu_cpu_iBus_cmd_m2sPipe_ready = ibus_ACK; // @[Cpu.scala 102:17]
  assign ibus_WE = 1'b0; // @[Cpu.scala 102:17]
  assign ibus_ADR = (cpu_1_cpu_1_iBus_cmd_m2sPipe_payload_pc >>> 2); // @[Cpu.scala 102:17]
  assign ibus_DAT_MOSI = 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx; // @[Cpu.scala 102:17]
  assign ibus_SEL = 4'b1111; // @[Cpu.scala 102:17]
  assign ibus_CTI = 3'b000; // @[Cpu.scala 102:17]
  assign ibus_BTE = 2'b00; // @[Cpu.scala 102:17]
  assign cpu_1_cpu_1_dBus_cmd_halfPipe_fire = (cpu_1_cpu_1_dBus_cmd_halfPipe_valid && cpu_1_cpu_1_dBus_cmd_halfPipe_ready); // @[BaseType.scala 305:24]
  assign cpu_1_dBus_cmd_ready = (! cpu_1_cpu_1_dBus_cmd_rValid); // @[Stream.scala 414:16]
  assign cpu_1_cpu_1_dBus_cmd_halfPipe_valid = cpu_1_cpu_1_dBus_cmd_rValid; // @[Stream.scala 416:20]
  assign cpu_1_cpu_1_dBus_cmd_halfPipe_payload_wr = cpu_1_cpu_1_dBus_cmd_rData_wr; // @[Stream.scala 417:22]
  assign cpu_1_cpu_1_dBus_cmd_halfPipe_payload_address = cpu_1_cpu_1_dBus_cmd_rData_address; // @[Stream.scala 417:22]
  assign cpu_1_cpu_1_dBus_cmd_halfPipe_payload_data = cpu_1_cpu_1_dBus_cmd_rData_data; // @[Stream.scala 417:22]
  assign cpu_1_cpu_1_dBus_cmd_halfPipe_payload_size = cpu_1_cpu_1_dBus_cmd_rData_size; // @[Stream.scala 417:22]
  always @(*) begin
    case(cpu_1_cpu_1_dBus_cmd_halfPipe_payload_size)
      2'b00 : begin
        _zz_dbus_SEL_1 = 4'b0001; // @[Misc.scala 239:22]
      end
      2'b01 : begin
        _zz_dbus_SEL_1 = 4'b0011; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_dbus_SEL_1 = 4'b1111; // @[Misc.scala 235:22]
      end
    endcase
  end

  always @(*) begin
    _zz_dbus_SEL = (_zz_dbus_SEL_1 <<< cpu_1_cpu_1_dBus_cmd_halfPipe_payload_address[1 : 0]); // @[DBusSimplePlugin.scala 188:13]
    if(when_DBusSimplePlugin_l189) begin
      _zz_dbus_SEL = 4'b1111; // @[Bits.scala 131:40]
    end
  end

  assign when_DBusSimplePlugin_l189 = (! cpu_1_cpu_1_dBus_cmd_halfPipe_payload_wr); // @[BaseType.scala 299:24]
  assign _zz_dbus_WE = cpu_1_cpu_1_dBus_cmd_halfPipe_payload_wr; // @[DBusSimplePlugin.scala 192:13]
  assign cpu_1_cpu_1_dBus_cmd_halfPipe_ready = (cpu_1_cpu_1_dBus_cmd_halfPipe_valid && _zz_cpu_cpu_dBus_cmd_halfPipe_ready); // @[DBusSimplePlugin.scala 195:20]
  assign cpu_1_dBus_rsp_ready = ((cpu_1_cpu_1_dBus_cmd_halfPipe_valid && (! _zz_dbus_WE)) && _zz_cpu_cpu_dBus_cmd_halfPipe_ready); // @[DBusSimplePlugin.scala 199:15]
  assign dbus_CYC = cpu_1_cpu_1_dBus_cmd_halfPipe_valid; // @[Cpu.scala 103:17]
  assign dbus_STB = cpu_1_cpu_1_dBus_cmd_halfPipe_valid; // @[Cpu.scala 103:17]
  assign _zz_cpu_cpu_dBus_cmd_halfPipe_ready = dbus_ACK; // @[Cpu.scala 103:17]
  assign dbus_WE = _zz_dbus_WE; // @[Cpu.scala 103:17]
  assign dbus_ADR = (cpu_1_cpu_1_dBus_cmd_halfPipe_payload_address >>> 2); // @[Cpu.scala 103:17]
  assign dbus_DAT_MOSI = cpu_1_cpu_1_dBus_cmd_halfPipe_payload_data; // @[Cpu.scala 103:17]
  assign dbus_SEL = _zz_dbus_SEL; // @[Cpu.scala 103:17]
  assign dbus_CTI = 3'b000; // @[Cpu.scala 103:17]
  assign dbus_BTE = 2'b00; // @[Cpu.scala 103:17]
  always @(posedge clk) begin
    if(reset) begin
      cpu_1_cpu_1_iBus_cmd_rValid <= 1'b0; // @[Data.scala 400:33]
      cpu_1_cpu_1_dBus_cmd_rValid <= 1'b0; // @[Data.scala 400:33]
    end else begin
      if(cpu_1_iBus_cmd_ready) begin
        cpu_1_cpu_1_iBus_cmd_rValid <= cpu_1_iBus_cmd_valid; // @[Stream.scala 361:29]
      end
      if(cpu_1_dBus_cmd_valid) begin
        cpu_1_cpu_1_dBus_cmd_rValid <= 1'b1; // @[Stream.scala 411:33]
      end
      if(cpu_1_cpu_1_dBus_cmd_halfPipe_fire) begin
        cpu_1_cpu_1_dBus_cmd_rValid <= 1'b0; // @[Stream.scala 411:53]
      end
    end
  end

  always @(posedge clk) begin
    if(cpu_1_iBus_cmd_ready) begin
      cpu_1_cpu_1_iBus_cmd_rData_pc <= cpu_1_iBus_cmd_payload_pc; // @[Stream.scala 362:28]
    end
    if(cpu_1_dBus_cmd_ready) begin
      cpu_1_cpu_1_dBus_cmd_rData_wr <= cpu_1_dBus_cmd_payload_wr; // @[Stream.scala 412:28]
      cpu_1_cpu_1_dBus_cmd_rData_address <= cpu_1_dBus_cmd_payload_address; // @[Stream.scala 412:28]
      cpu_1_cpu_1_dBus_cmd_rData_data <= cpu_1_dBus_cmd_payload_data; // @[Stream.scala 412:28]
      cpu_1_cpu_1_dBus_cmd_rData_size <= cpu_1_dBus_cmd_payload_size; // @[Stream.scala 412:28]
    end
  end


endmodule

//SimpleEncoder_2 replaced by SimpleEncoder_2

//SimpleEncoder_2 replaced by SimpleEncoder_2

module SimpleEncoder_2 (
  input               inputs_0,
  input               inputs_1,
  output reg [0:0]    output_1,
  output reg          isValid
);

  wire       [1:0]    _zz_switch_SimpleEncoder_l13;
  wire       [1:0]    switch_SimpleEncoder_l13;

  assign _zz_switch_SimpleEncoder_l13 = {inputs_1,inputs_0};
  assign switch_SimpleEncoder_l13 = _zz_switch_SimpleEncoder_l13[1 : 0]; // @[BaseType.scala 299:24]
  always @(*) begin
    casez(switch_SimpleEncoder_l13)
      2'b01 : begin
        output_1 = 1'b0; // @[SimpleEncoder.scala 16:43]
      end
      2'b10 : begin
        output_1 = 1'b1; // @[SimpleEncoder.scala 16:43]
      end
      default : begin
        output_1 = 1'b0; // @[SimpleEncoder.scala 22:35]
      end
    endcase
  end

  always @(*) begin
    casez(switch_SimpleEncoder_l13)
      2'b01 : begin
        isValid = 1'b1; // @[SimpleEncoder.scala 17:44]
      end
      2'b10 : begin
        isValid = 1'b1; // @[SimpleEncoder.scala 17:44]
      end
      default : begin
        isValid = 1'b0; // @[SimpleEncoder.scala 23:36]
      end
    endcase
  end


endmodule

//WishboneAdapter_1 replaced by WishboneAdapter_1

module WishboneAdapter_1 (
  input               io_wbm_CYC,
  input               io_wbm_STB,
  output              io_wbm_ACK,
  input               io_wbm_WE,
  input      [29:0]   io_wbm_ADR,
  output     [31:0]   io_wbm_DAT_MISO,
  input      [31:0]   io_wbm_DAT_MOSI,
  input      [3:0]    io_wbm_SEL,
  output              io_wbm_ERR,
  input      [2:0]    io_wbm_CTI,
  input      [1:0]    io_wbm_BTE,
  output              io_wbs_CYC,
  output              io_wbs_STB,
  input               io_wbs_ACK,
  output              io_wbs_WE,
  output     [29:0]   io_wbs_ADR,
  input      [31:0]   io_wbs_DAT_MISO,
  output     [31:0]   io_wbs_DAT_MOSI,
  output     [3:0]    io_wbs_SEL
);


  assign io_wbs_CYC = io_wbm_CYC; // @[Wishbone.scala 197:19]
  assign io_wbs_STB = io_wbm_STB; // @[Wishbone.scala 198:19]
  assign io_wbs_WE = io_wbm_WE; // @[Wishbone.scala 199:19]
  assign io_wbm_ACK = io_wbs_ACK; // @[Wishbone.scala 200:19]
  assign io_wbs_ADR = io_wbm_ADR; // @[Wishbone.scala 257:33]
  assign io_wbs_DAT_MOSI = io_wbm_DAT_MOSI; // @[Wishbone.scala 257:33]
  assign io_wbm_DAT_MISO = io_wbs_DAT_MISO; // @[Wishbone.scala 257:33]
  assign io_wbs_SEL = io_wbm_SEL; // @[Wishbone.scala 257:33]

endmodule

module VexRiscv (
  output              iBus_cmd_valid,
  input               iBus_cmd_ready,
  output     [31:0]   iBus_cmd_payload_pc,
  input               iBus_rsp_valid,
  input               iBus_rsp_payload_error,
  input      [31:0]   iBus_rsp_payload_inst,
  input               timerInterrupt,
  input               externalInterrupt,
  input               softwareInterrupt,
  output              dBus_cmd_valid,
  input               dBus_cmd_ready,
  output              dBus_cmd_payload_wr,
  output     [31:0]   dBus_cmd_payload_address,
  output     [31:0]   dBus_cmd_payload_data,
  output     [1:0]    dBus_cmd_payload_size,
  input               dBus_rsp_ready,
  input               dBus_rsp_error,
  input      [31:0]   dBus_rsp_data,
  input               clk,
  input               reset
);
  localparam BranchCtrlEnum_INC = 2'd0;
  localparam BranchCtrlEnum_B = 2'd1;
  localparam BranchCtrlEnum_JAL = 2'd2;
  localparam BranchCtrlEnum_JALR = 2'd3;
  localparam ShiftCtrlEnum_DISABLE_1 = 2'd0;
  localparam ShiftCtrlEnum_SLL_1 = 2'd1;
  localparam ShiftCtrlEnum_SRL_1 = 2'd2;
  localparam ShiftCtrlEnum_SRA_1 = 2'd3;
  localparam AluBitwiseCtrlEnum_XOR_1 = 2'd0;
  localparam AluBitwiseCtrlEnum_OR_1 = 2'd1;
  localparam AluBitwiseCtrlEnum_AND_1 = 2'd2;
  localparam AluCtrlEnum_ADD_SUB = 2'd0;
  localparam AluCtrlEnum_SLT_SLTU = 2'd1;
  localparam AluCtrlEnum_BITWISE = 2'd2;
  localparam EnvCtrlEnum_NONE = 1'd0;
  localparam EnvCtrlEnum_XRET = 1'd1;
  localparam Src2CtrlEnum_RS = 2'd0;
  localparam Src2CtrlEnum_IMI = 2'd1;
  localparam Src2CtrlEnum_IMS = 2'd2;
  localparam Src2CtrlEnum_PC = 2'd3;
  localparam Src1CtrlEnum_RS = 2'd0;
  localparam Src1CtrlEnum_IMU = 2'd1;
  localparam Src1CtrlEnum_PC_INCREMENT = 2'd2;
  localparam Src1CtrlEnum_URS1 = 2'd3;

  wire                IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_ready;
  reg        [31:0]   _zz_RegFilePlugin_regFile_port0;
  reg        [31:0]   _zz_RegFilePlugin_regFile_port1;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_c_io_push_ready;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_inst;
  wire       [0:0]    IBusSimplePlugin_rspJoin_rspBuffer_c_io_occupancy;
  wire       [1:0]    _zz_IBusSimplePlugin_jump_pcLoad_payload_1;
  wire       [1:0]    _zz_IBusSimplePlugin_jump_pcLoad_payload_2;
  wire       [31:0]   _zz_IBusSimplePlugin_fetchPc_pc;
  wire       [2:0]    _zz_IBusSimplePlugin_fetchPc_pc_1;
  wire       [0:0]    _zz_IBusSimplePlugin_pending_next;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_1;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_2;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_3;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_4;
  wire       [1:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_5;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_6;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_7;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_8;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_9;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_10;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_11;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_12;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_13;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_14;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_15;
  wire       [18:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_16;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_17;
  wire       [1:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_18;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_19;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_20;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_21;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_22;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_23;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_24;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_25;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_26;
  wire       [14:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_27;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_28;
  wire       [1:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_29;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_30;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_31;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_32;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_33;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_34;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_35;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_36;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_37;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_38;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_39;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_40;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_41;
  wire       [10:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_42;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_43;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_44;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_45;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_46;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_47;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_48;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_49;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_50;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_51;
  wire       [4:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_52;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_53;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_54;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_55;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_56;
  wire       [1:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_57;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_58;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_59;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_60;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_61;
  wire       [6:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_62;
  wire       [1:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_63;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_64;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_65;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_66;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_67;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_68;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_69;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_70;
  wire       [2:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_71;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_72;
  wire       [3:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_73;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_74;
  wire       [31:0]   _zz__zz_decode_SRC_LESS_UNSIGNED_75;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_76;
  wire                _zz__zz_decode_SRC_LESS_UNSIGNED_77;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_78;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_79;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_80;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_81;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_82;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_83;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_84;
  wire       [0:0]    _zz__zz_decode_SRC_LESS_UNSIGNED_85;
  wire                _zz_RegFilePlugin_regFile_port;
  wire                _zz_decode_RegFilePlugin_rs1Data;
  wire                _zz_RegFilePlugin_regFile_port_1;
  wire                _zz_decode_RegFilePlugin_rs2Data;
  wire       [0:0]    _zz__zz_execute_REGFILE_WRITE_DATA;
  wire       [2:0]    _zz__zz_decode_SRC1;
  wire       [4:0]    _zz__zz_decode_SRC1_1;
  wire       [11:0]   _zz__zz_decode_SRC2_2;
  wire       [31:0]   _zz_execute_SrcPlugin_addSub;
  wire       [31:0]   _zz_execute_SrcPlugin_addSub_1;
  wire       [31:0]   _zz_execute_SrcPlugin_addSub_2;
  wire       [31:0]   _zz_execute_SrcPlugin_addSub_3;
  wire       [31:0]   _zz_execute_SrcPlugin_addSub_4;
  wire       [31:0]   _zz_execute_SrcPlugin_addSub_5;
  wire       [31:0]   _zz_execute_SrcPlugin_addSub_6;
  wire       [31:0]   _zz__zz_execute_to_memory_REGFILE_WRITE_DATA_1;
  wire       [32:0]   _zz__zz_execute_to_memory_REGFILE_WRITE_DATA_1_1;
  wire       [19:0]   _zz__zz_execute_BranchPlugin_branch_src2;
  wire       [11:0]   _zz__zz_execute_BranchPlugin_branch_src2_4;
  wire       [31:0]   memory_MEMORY_READ_DATA;
  wire       [31:0]   execute_BRANCH_CALC;
  wire                execute_BRANCH_DO;
  wire       [31:0]   writeBack_REGFILE_WRITE_DATA;
  wire       [31:0]   execute_REGFILE_WRITE_DATA;
  wire       [1:0]    memory_MEMORY_ADDRESS_LOW;
  wire       [1:0]    execute_MEMORY_ADDRESS_LOW;
  wire       [31:0]   decode_SRC2;
  wire       [31:0]   decode_SRC1;
  wire                decode_SRC2_FORCE_ZERO;
  wire       [31:0]   decode_RS2;
  wire       [31:0]   decode_RS1;
  wire       [1:0]    decode_BRANCH_CTRL;
  wire       [1:0]    _zz_decode_BRANCH_CTRL;
  wire       [1:0]    _zz_decode_to_execute_BRANCH_CTRL;
  wire       [1:0]    _zz_decode_to_execute_BRANCH_CTRL_1;
  wire       [1:0]    decode_SHIFT_CTRL;
  wire       [1:0]    _zz_decode_SHIFT_CTRL;
  wire       [1:0]    _zz_decode_to_execute_SHIFT_CTRL;
  wire       [1:0]    _zz_decode_to_execute_SHIFT_CTRL_1;
  wire       [1:0]    decode_ALU_BITWISE_CTRL;
  wire       [1:0]    _zz_decode_ALU_BITWISE_CTRL;
  wire       [1:0]    _zz_decode_to_execute_ALU_BITWISE_CTRL;
  wire       [1:0]    _zz_decode_to_execute_ALU_BITWISE_CTRL_1;
  wire                decode_SRC_LESS_UNSIGNED;
  wire       [1:0]    decode_ALU_CTRL;
  wire       [1:0]    _zz_decode_ALU_CTRL;
  wire       [1:0]    _zz_decode_to_execute_ALU_CTRL;
  wire       [1:0]    _zz_decode_to_execute_ALU_CTRL_1;
  wire       [0:0]    _zz_memory_to_writeBack_ENV_CTRL;
  wire       [0:0]    _zz_memory_to_writeBack_ENV_CTRL_1;
  wire       [0:0]    _zz_execute_to_memory_ENV_CTRL;
  wire       [0:0]    _zz_execute_to_memory_ENV_CTRL_1;
  wire       [0:0]    decode_ENV_CTRL;
  wire       [0:0]    _zz_decode_ENV_CTRL;
  wire       [0:0]    _zz_decode_to_execute_ENV_CTRL;
  wire       [0:0]    _zz_decode_to_execute_ENV_CTRL_1;
  wire                decode_IS_CSR;
  wire                decode_MEMORY_STORE;
  wire                execute_BYPASSABLE_MEMORY_STAGE;
  wire                decode_BYPASSABLE_MEMORY_STAGE;
  wire                decode_BYPASSABLE_EXECUTE_STAGE;
  wire                decode_MEMORY_ENABLE;
  wire                decode_CSR_READ_OPCODE;
  wire                decode_CSR_WRITE_OPCODE;
  wire       [31:0]   writeBack_FORMAL_PC_NEXT;
  wire       [31:0]   memory_FORMAL_PC_NEXT;
  wire       [31:0]   execute_FORMAL_PC_NEXT;
  wire       [31:0]   decode_FORMAL_PC_NEXT;
  wire       [31:0]   memory_PC;
  wire       [31:0]   memory_BRANCH_CALC;
  wire                memory_BRANCH_DO;
  wire       [31:0]   execute_PC;
  wire       [31:0]   execute_RS1;
  wire       [1:0]    execute_BRANCH_CTRL;
  wire       [1:0]    _zz_execute_BRANCH_CTRL;
  wire                decode_RS2_USE;
  wire                decode_RS1_USE;
  wire                execute_REGFILE_WRITE_VALID;
  wire                execute_BYPASSABLE_EXECUTE_STAGE;
  wire                memory_REGFILE_WRITE_VALID;
  wire       [31:0]   memory_INSTRUCTION;
  wire                memory_BYPASSABLE_MEMORY_STAGE;
  wire                writeBack_REGFILE_WRITE_VALID;
  wire       [31:0]   memory_REGFILE_WRITE_DATA;
  wire       [1:0]    execute_SHIFT_CTRL;
  wire       [1:0]    _zz_execute_SHIFT_CTRL;
  wire                execute_SRC_LESS_UNSIGNED;
  wire                execute_SRC2_FORCE_ZERO;
  wire                execute_SRC_USE_SUB_LESS;
  wire       [31:0]   _zz_decode_to_execute_PC;
  wire       [31:0]   _zz_decode_to_execute_RS2;
  wire       [1:0]    decode_SRC2_CTRL;
  wire       [1:0]    _zz_decode_SRC2_CTRL;
  wire       [31:0]   _zz_decode_to_execute_RS1;
  wire       [1:0]    decode_SRC1_CTRL;
  wire       [1:0]    _zz_decode_SRC1_CTRL;
  wire                decode_SRC_USE_SUB_LESS;
  wire                decode_SRC_ADD_ZERO;
  wire       [31:0]   execute_SRC_ADD_SUB;
  wire                execute_SRC_LESS;
  wire       [1:0]    execute_ALU_CTRL;
  wire       [1:0]    _zz_execute_ALU_CTRL;
  wire       [31:0]   execute_SRC2;
  wire       [1:0]    execute_ALU_BITWISE_CTRL;
  wire       [1:0]    _zz_execute_ALU_BITWISE_CTRL;
  wire       [31:0]   _zz_lastStageRegFileWrite_payload_address;
  wire                _zz_lastStageRegFileWrite_valid;
  reg                 _zz_1;
  wire       [31:0]   decode_INSTRUCTION_ANTICIPATED;
  reg                 decode_REGFILE_WRITE_VALID;
  wire       [1:0]    _zz_decode_BRANCH_CTRL_1;
  wire       [1:0]    _zz_decode_SHIFT_CTRL_1;
  wire       [1:0]    _zz_decode_ALU_BITWISE_CTRL_1;
  wire       [1:0]    _zz_decode_ALU_CTRL_1;
  wire       [0:0]    _zz_decode_ENV_CTRL_1;
  wire       [1:0]    _zz_decode_SRC2_CTRL_1;
  wire       [1:0]    _zz_decode_SRC1_CTRL_1;
  reg        [31:0]   _zz_execute_to_memory_REGFILE_WRITE_DATA;
  wire       [31:0]   execute_SRC1;
  wire                execute_CSR_READ_OPCODE;
  wire                execute_CSR_WRITE_OPCODE;
  wire                execute_IS_CSR;
  wire       [0:0]    memory_ENV_CTRL;
  wire       [0:0]    _zz_memory_ENV_CTRL;
  wire       [0:0]    execute_ENV_CTRL;
  wire       [0:0]    _zz_execute_ENV_CTRL;
  wire       [0:0]    writeBack_ENV_CTRL;
  wire       [0:0]    _zz_writeBack_ENV_CTRL;
  reg        [31:0]   _zz_lastStageRegFileWrite_payload_data;
  wire                writeBack_MEMORY_ENABLE;
  wire       [1:0]    writeBack_MEMORY_ADDRESS_LOW;
  wire       [31:0]   writeBack_MEMORY_READ_DATA;
  wire                memory_MEMORY_STORE;
  wire                memory_MEMORY_ENABLE;
  wire       [31:0]   execute_SRC_ADD;
  wire       [31:0]   execute_RS2;
  wire       [31:0]   execute_INSTRUCTION;
  wire                execute_MEMORY_STORE;
  wire                execute_MEMORY_ENABLE;
  wire                execute_ALIGNEMENT_FAULT;
  reg        [31:0]   _zz_memory_to_writeBack_FORMAL_PC_NEXT;
  wire       [31:0]   decode_PC;
  wire       [31:0]   decode_INSTRUCTION;
  wire       [31:0]   writeBack_PC;
  wire       [31:0]   writeBack_INSTRUCTION;
  wire                decode_arbitration_haltItself;
  reg                 decode_arbitration_haltByOther;
  reg                 decode_arbitration_removeIt;
  wire                decode_arbitration_flushIt;
  wire                decode_arbitration_flushNext;
  reg                 decode_arbitration_isValid;
  wire                decode_arbitration_isStuck;
  wire                decode_arbitration_isStuckByOthers;
  wire                decode_arbitration_isFlushed;
  wire                decode_arbitration_isMoving;
  wire                decode_arbitration_isFiring;
  reg                 execute_arbitration_haltItself;
  wire                execute_arbitration_haltByOther;
  reg                 execute_arbitration_removeIt;
  wire                execute_arbitration_flushIt;
  wire                execute_arbitration_flushNext;
  reg                 execute_arbitration_isValid;
  wire                execute_arbitration_isStuck;
  wire                execute_arbitration_isStuckByOthers;
  wire                execute_arbitration_isFlushed;
  wire                execute_arbitration_isMoving;
  wire                execute_arbitration_isFiring;
  reg                 memory_arbitration_haltItself;
  wire                memory_arbitration_haltByOther;
  reg                 memory_arbitration_removeIt;
  wire                memory_arbitration_flushIt;
  reg                 memory_arbitration_flushNext;
  reg                 memory_arbitration_isValid;
  wire                memory_arbitration_isStuck;
  wire                memory_arbitration_isStuckByOthers;
  wire                memory_arbitration_isFlushed;
  wire                memory_arbitration_isMoving;
  wire                memory_arbitration_isFiring;
  wire                writeBack_arbitration_haltItself;
  wire                writeBack_arbitration_haltByOther;
  reg                 writeBack_arbitration_removeIt;
  wire                writeBack_arbitration_flushIt;
  reg                 writeBack_arbitration_flushNext;
  reg                 writeBack_arbitration_isValid;
  wire                writeBack_arbitration_isStuck;
  wire                writeBack_arbitration_isStuckByOthers;
  wire                writeBack_arbitration_isFlushed;
  wire                writeBack_arbitration_isMoving;
  wire                writeBack_arbitration_isFiring;
  wire       [31:0]   lastStageInstruction /* verilator public */ ;
  wire       [31:0]   lastStagePc /* verilator public */ ;
  wire                lastStageIsValid /* verilator public */ ;
  wire                lastStageIsFiring /* verilator public */ ;
  reg                 IBusSimplePlugin_fetcherHalt;
  wire                IBusSimplePlugin_forceNoDecodeCond;
  reg                 IBusSimplePlugin_incomingInstruction;
  wire                IBusSimplePlugin_pcValids_0;
  wire                IBusSimplePlugin_pcValids_1;
  wire                IBusSimplePlugin_pcValids_2;
  wire                IBusSimplePlugin_pcValids_3;
  wire       [31:0]   CsrPlugin_csrMapping_readDataSignal;
  wire       [31:0]   CsrPlugin_csrMapping_readDataInit;
  wire       [31:0]   CsrPlugin_csrMapping_writeDataSignal;
  wire                CsrPlugin_csrMapping_allowCsrSignal;
  wire                CsrPlugin_csrMapping_hazardFree;
  wire                CsrPlugin_inWfi /* verilator public */ ;
  wire                CsrPlugin_thirdPartyWake;
  reg                 CsrPlugin_jumpInterface_valid;
  reg        [31:0]   CsrPlugin_jumpInterface_payload;
  wire                CsrPlugin_exceptionPendings_0;
  wire                CsrPlugin_exceptionPendings_1;
  wire                CsrPlugin_exceptionPendings_2;
  wire                CsrPlugin_exceptionPendings_3;
  wire                contextSwitching;
  reg        [1:0]    CsrPlugin_privilege;
  wire                CsrPlugin_forceMachineWire;
  wire                CsrPlugin_allowInterrupts;
  wire                CsrPlugin_allowException;
  wire                CsrPlugin_allowEbreakException;
  wire                CsrPlugin_xretAwayFromMachine;
  wire                BranchPlugin_jumpInterface_valid;
  wire       [31:0]   BranchPlugin_jumpInterface_payload;
  wire                BranchPlugin_inDebugNoFetchFlag;
  wire                IBusSimplePlugin_externalFlush;
  wire                IBusSimplePlugin_jump_pcLoad_valid;
  wire       [31:0]   IBusSimplePlugin_jump_pcLoad_payload;
  wire       [1:0]    _zz_IBusSimplePlugin_jump_pcLoad_payload;
  wire                IBusSimplePlugin_fetchPc_output_valid;
  wire                IBusSimplePlugin_fetchPc_output_ready;
  wire       [31:0]   IBusSimplePlugin_fetchPc_output_payload;
  reg        [31:0]   IBusSimplePlugin_fetchPc_pcReg /* verilator public */ ;
  reg                 IBusSimplePlugin_fetchPc_correction;
  reg                 IBusSimplePlugin_fetchPc_correctionReg;
  wire                IBusSimplePlugin_fetchPc_output_fire;
  wire                IBusSimplePlugin_fetchPc_corrected;
  reg                 IBusSimplePlugin_fetchPc_pcRegPropagate;
  reg                 IBusSimplePlugin_fetchPc_booted;
  reg                 IBusSimplePlugin_fetchPc_inc;
  wire                when_Fetcher_l134;
  wire                IBusSimplePlugin_fetchPc_output_fire_1;
  wire                when_Fetcher_l134_1;
  reg        [31:0]   IBusSimplePlugin_fetchPc_pc;
  reg                 IBusSimplePlugin_fetchPc_flushed;
  wire                when_Fetcher_l161;
  wire                IBusSimplePlugin_iBusRsp_redoFetch;
  wire                IBusSimplePlugin_iBusRsp_stages_0_input_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_0_input_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_0_input_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_0_output_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_0_output_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_0_output_payload;
  reg                 IBusSimplePlugin_iBusRsp_stages_0_halt;
  wire                IBusSimplePlugin_iBusRsp_stages_1_input_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_1_input_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_1_input_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_1_output_valid;
  wire                IBusSimplePlugin_iBusRsp_stages_1_output_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_stages_1_output_payload;
  wire                IBusSimplePlugin_iBusRsp_stages_1_halt;
  wire                _zz_IBusSimplePlugin_iBusRsp_stages_0_input_ready;
  wire                _zz_IBusSimplePlugin_iBusRsp_stages_1_input_ready;
  wire                IBusSimplePlugin_iBusRsp_flush;
  wire                _zz_IBusSimplePlugin_iBusRsp_stages_0_output_ready;
  wire                _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid;
  reg                 _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid_1;
  reg                 IBusSimplePlugin_iBusRsp_readyForError;
  wire                IBusSimplePlugin_iBusRsp_output_valid;
  wire                IBusSimplePlugin_iBusRsp_output_ready;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_output_payload_pc;
  wire                IBusSimplePlugin_iBusRsp_output_payload_rsp_error;
  wire       [31:0]   IBusSimplePlugin_iBusRsp_output_payload_rsp_inst;
  wire                IBusSimplePlugin_iBusRsp_output_payload_isRvc;
  wire                IBusSimplePlugin_injector_decodeInput_valid;
  wire                IBusSimplePlugin_injector_decodeInput_ready;
  wire       [31:0]   IBusSimplePlugin_injector_decodeInput_payload_pc;
  wire                IBusSimplePlugin_injector_decodeInput_payload_rsp_error;
  wire       [31:0]   IBusSimplePlugin_injector_decodeInput_payload_rsp_inst;
  wire                IBusSimplePlugin_injector_decodeInput_payload_isRvc;
  reg                 _zz_IBusSimplePlugin_injector_decodeInput_valid;
  reg        [31:0]   _zz_IBusSimplePlugin_injector_decodeInput_payload_pc;
  reg                 _zz_IBusSimplePlugin_injector_decodeInput_payload_rsp_error;
  reg        [31:0]   _zz_IBusSimplePlugin_injector_decodeInput_payload_rsp_inst;
  reg                 _zz_IBusSimplePlugin_injector_decodeInput_payload_isRvc;
  wire                when_Fetcher_l323;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_0;
  wire                when_Fetcher_l332;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_1;
  wire                when_Fetcher_l332_1;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_2;
  wire                when_Fetcher_l332_2;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_3;
  wire                when_Fetcher_l332_3;
  reg                 IBusSimplePlugin_injector_nextPcCalc_valids_4;
  wire                when_Fetcher_l332_4;
  reg        [31:0]   IBusSimplePlugin_injector_formal_rawInDecode;
  wire                IBusSimplePlugin_cmd_valid;
  wire                IBusSimplePlugin_cmd_ready;
  wire       [31:0]   IBusSimplePlugin_cmd_payload_pc;
  wire                IBusSimplePlugin_cmd_s2mPipe_valid;
  wire                IBusSimplePlugin_cmd_s2mPipe_ready;
  wire       [31:0]   IBusSimplePlugin_cmd_s2mPipe_payload_pc;
  reg                 IBusSimplePlugin_cmd_rValid;
  reg        [31:0]   IBusSimplePlugin_cmd_rData_pc;
  wire                IBusSimplePlugin_pending_inc;
  wire                IBusSimplePlugin_pending_dec;
  reg        [0:0]    IBusSimplePlugin_pending_value;
  wire       [0:0]    IBusSimplePlugin_pending_next;
  wire                IBusSimplePlugin_cmdFork_canEmit;
  wire                when_IBusSimplePlugin_l305;
  wire                IBusSimplePlugin_cmd_fire;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_output_valid;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_output_ready;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_output_payload_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_rspBuffer_output_payload_inst;
  reg        [0:0]    IBusSimplePlugin_rspJoin_rspBuffer_discardCounter;
  wire                iBus_rsp_toStream_valid;
  wire                iBus_rsp_toStream_ready;
  wire                iBus_rsp_toStream_payload_error;
  wire       [31:0]   iBus_rsp_toStream_payload_inst;
  wire                IBusSimplePlugin_rspJoin_rspBuffer_flush;
  wire                cpu_1_IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_fire;
  wire       [31:0]   IBusSimplePlugin_rspJoin_fetchRsp_pc;
  reg                 IBusSimplePlugin_rspJoin_fetchRsp_rsp_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_fetchRsp_rsp_inst;
  wire                IBusSimplePlugin_rspJoin_fetchRsp_isRvc;
  wire                when_IBusSimplePlugin_l376;
  wire                IBusSimplePlugin_rspJoin_join_valid;
  wire                IBusSimplePlugin_rspJoin_join_ready;
  wire       [31:0]   IBusSimplePlugin_rspJoin_join_payload_pc;
  wire                IBusSimplePlugin_rspJoin_join_payload_rsp_error;
  wire       [31:0]   IBusSimplePlugin_rspJoin_join_payload_rsp_inst;
  wire                IBusSimplePlugin_rspJoin_join_payload_isRvc;
  wire                IBusSimplePlugin_rspJoin_exceptionDetected;
  wire                IBusSimplePlugin_rspJoin_join_fire;
  wire                IBusSimplePlugin_rspJoin_join_fire_1;
  wire                _zz_IBusSimplePlugin_iBusRsp_output_valid;
  wire                _zz_dBus_cmd_valid;
  reg                 execute_DBusSimplePlugin_skipCmd;
  reg        [31:0]   _zz_dBus_cmd_payload_data;
  wire                when_DBusSimplePlugin_l428;
  reg        [3:0]    _zz_execute_DBusSimplePlugin_formalMask;
  wire       [3:0]    execute_DBusSimplePlugin_formalMask;
  wire                when_DBusSimplePlugin_l482;
  reg        [31:0]   writeBack_DBusSimplePlugin_rspShifted;
  wire       [1:0]    switch_Misc_l226;
  wire                _zz_writeBack_DBusSimplePlugin_rspFormated;
  reg        [31:0]   _zz_writeBack_DBusSimplePlugin_rspFormated_1;
  wire                _zz_writeBack_DBusSimplePlugin_rspFormated_2;
  reg        [31:0]   _zz_writeBack_DBusSimplePlugin_rspFormated_3;
  reg        [31:0]   writeBack_DBusSimplePlugin_rspFormated;
  wire                when_DBusSimplePlugin_l558;
  wire       [1:0]    CsrPlugin_misa_base;
  wire       [25:0]   CsrPlugin_misa_extensions;
  wire       [1:0]    CsrPlugin_mtvec_mode;
  wire       [29:0]   CsrPlugin_mtvec_base;
  reg        [31:0]   CsrPlugin_mepc;
  reg                 CsrPlugin_mstatus_MIE;
  reg                 CsrPlugin_mstatus_MPIE;
  reg        [1:0]    CsrPlugin_mstatus_MPP;
  reg                 CsrPlugin_mip_MEIP;
  reg                 CsrPlugin_mip_MTIP;
  reg                 CsrPlugin_mip_MSIP;
  reg                 CsrPlugin_mie_MEIE;
  reg                 CsrPlugin_mie_MTIE;
  reg                 CsrPlugin_mie_MSIE;
  reg                 CsrPlugin_mcause_interrupt;
  reg        [3:0]    CsrPlugin_mcause_exceptionCode;
  reg        [31:0]   CsrPlugin_mtval;
  reg        [63:0]   CsrPlugin_mcycle;
  reg        [63:0]   CsrPlugin_minstret;
  wire                _zz_when_CsrPlugin_l1224;
  wire                _zz_when_CsrPlugin_l1224_1;
  wire                _zz_when_CsrPlugin_l1224_2;
  reg                 CsrPlugin_interrupt_valid;
  reg        [3:0]    CsrPlugin_interrupt_code /* verilator public */ ;
  reg        [1:0]    CsrPlugin_interrupt_targetPrivilege;
  wire                when_CsrPlugin_l1218;
  wire                when_CsrPlugin_l1224;
  wire                when_CsrPlugin_l1224_1;
  wire                when_CsrPlugin_l1224_2;
  wire                CsrPlugin_exception;
  wire                CsrPlugin_lastStageWasWfi;
  reg                 CsrPlugin_pipelineLiberator_pcValids_0;
  reg                 CsrPlugin_pipelineLiberator_pcValids_1;
  reg                 CsrPlugin_pipelineLiberator_pcValids_2;
  wire                CsrPlugin_pipelineLiberator_active;
  wire                when_CsrPlugin_l1257;
  wire                when_CsrPlugin_l1257_1;
  wire                when_CsrPlugin_l1257_2;
  wire                when_CsrPlugin_l1262;
  reg                 CsrPlugin_pipelineLiberator_done;
  wire                CsrPlugin_interruptJump /* verilator public */ ;
  reg                 CsrPlugin_hadException /* verilator public */ ;
  wire       [1:0]    CsrPlugin_targetPrivilege;
  wire       [3:0]    CsrPlugin_trapCause;
  wire                CsrPlugin_trapCauseEbreakDebug;
  reg        [1:0]    CsrPlugin_xtvec_mode;
  reg        [29:0]   CsrPlugin_xtvec_base;
  wire                CsrPlugin_trapEnterDebug;
  wire                when_CsrPlugin_l1312;
  wire                when_CsrPlugin_l1320;
  wire                when_CsrPlugin_l1378;
  wire       [1:0]    switch_CsrPlugin_l1382;
  reg                 execute_CsrPlugin_wfiWake;
  wire                when_CsrPlugin_l1449;
  wire                execute_CsrPlugin_blockedBySideEffects;
  reg                 execute_CsrPlugin_illegalAccess;
  reg                 execute_CsrPlugin_illegalInstruction;
  wire                when_CsrPlugin_l1469;
  wire                when_CsrPlugin_l1470;
  reg                 execute_CsrPlugin_writeInstruction;
  reg                 execute_CsrPlugin_readInstruction;
  wire                execute_CsrPlugin_writeEnable;
  wire                execute_CsrPlugin_readEnable;
  wire       [31:0]   execute_CsrPlugin_readToWriteData;
  wire                switch_Misc_l226_1;
  reg        [31:0]   _zz_CsrPlugin_csrMapping_writeDataSignal;
  wire                when_CsrPlugin_l1509;
  wire                when_CsrPlugin_l1513;
  wire       [11:0]   execute_CsrPlugin_csrAddress;
  wire       [24:0]   _zz_decode_SRC_LESS_UNSIGNED;
  wire                _zz_decode_SRC_LESS_UNSIGNED_1;
  wire                _zz_decode_SRC_LESS_UNSIGNED_2;
  wire                _zz_decode_SRC_LESS_UNSIGNED_3;
  wire                _zz_decode_SRC_LESS_UNSIGNED_4;
  wire                _zz_decode_SRC_LESS_UNSIGNED_5;
  wire       [1:0]    _zz_decode_SRC1_CTRL_2;
  wire       [1:0]    _zz_decode_SRC2_CTRL_2;
  wire       [0:0]    _zz_decode_ENV_CTRL_2;
  wire       [1:0]    _zz_decode_ALU_CTRL_2;
  wire       [1:0]    _zz_decode_ALU_BITWISE_CTRL_2;
  wire       [1:0]    _zz_decode_SHIFT_CTRL_2;
  wire       [1:0]    _zz_decode_BRANCH_CTRL_2;
  wire                when_RegFilePlugin_l63;
  wire       [4:0]    decode_RegFilePlugin_regFileReadAddress1;
  wire       [4:0]    decode_RegFilePlugin_regFileReadAddress2;
  wire       [31:0]   decode_RegFilePlugin_rs1Data;
  wire       [31:0]   decode_RegFilePlugin_rs2Data;
  reg                 lastStageRegFileWrite_valid /* verilator public */ ;
  reg        [4:0]    lastStageRegFileWrite_payload_address /* verilator public */ ;
  reg        [31:0]   lastStageRegFileWrite_payload_data /* verilator public */ ;
  reg                 _zz_2;
  reg        [31:0]   execute_IntAluPlugin_bitwise;
  reg        [31:0]   _zz_execute_REGFILE_WRITE_DATA;
  reg        [31:0]   _zz_decode_SRC1;
  wire                _zz_decode_SRC2;
  reg        [19:0]   _zz_decode_SRC2_1;
  wire                _zz_decode_SRC2_2;
  reg        [19:0]   _zz_decode_SRC2_3;
  reg        [31:0]   _zz_decode_SRC2_4;
  reg        [31:0]   execute_SrcPlugin_addSub;
  wire                execute_SrcPlugin_less;
  reg                 execute_LightShifterPlugin_isActive;
  wire                execute_LightShifterPlugin_isShift;
  reg        [4:0]    execute_LightShifterPlugin_amplitudeReg;
  wire       [4:0]    execute_LightShifterPlugin_amplitude;
  wire       [31:0]   execute_LightShifterPlugin_shiftInput;
  wire                execute_LightShifterPlugin_done;
  wire                when_ShiftPlugins_l169;
  reg        [31:0]   _zz_execute_to_memory_REGFILE_WRITE_DATA_1;
  wire                when_ShiftPlugins_l175;
  wire                when_ShiftPlugins_l184;
  reg                 HazardSimplePlugin_src0Hazard;
  reg                 HazardSimplePlugin_src1Hazard;
  wire                HazardSimplePlugin_writeBackWrites_valid;
  wire       [4:0]    HazardSimplePlugin_writeBackWrites_payload_address;
  wire       [31:0]   HazardSimplePlugin_writeBackWrites_payload_data;
  reg                 HazardSimplePlugin_writeBackBuffer_valid;
  reg        [4:0]    HazardSimplePlugin_writeBackBuffer_payload_address;
  reg        [31:0]   HazardSimplePlugin_writeBackBuffer_payload_data;
  wire                HazardSimplePlugin_addr0Match;
  wire                HazardSimplePlugin_addr1Match;
  wire                when_HazardSimplePlugin_l59;
  wire                when_HazardSimplePlugin_l62;
  wire                when_HazardSimplePlugin_l57;
  wire                when_HazardSimplePlugin_l58;
  wire                when_HazardSimplePlugin_l59_1;
  wire                when_HazardSimplePlugin_l62_1;
  wire                when_HazardSimplePlugin_l57_1;
  wire                when_HazardSimplePlugin_l58_1;
  wire                when_HazardSimplePlugin_l59_2;
  wire                when_HazardSimplePlugin_l62_2;
  wire                when_HazardSimplePlugin_l57_2;
  wire                when_HazardSimplePlugin_l58_2;
  wire                when_HazardSimplePlugin_l105;
  wire                when_HazardSimplePlugin_l108;
  wire                when_HazardSimplePlugin_l113;
  wire                execute_BranchPlugin_eq;
  wire       [2:0]    switch_Misc_l226_2;
  reg                 _zz_execute_BRANCH_DO;
  reg                 _zz_execute_BRANCH_DO_1;
  wire       [31:0]   execute_BranchPlugin_branch_src1;
  wire                _zz_execute_BranchPlugin_branch_src2;
  reg        [10:0]   _zz_execute_BranchPlugin_branch_src2_1;
  wire                _zz_execute_BranchPlugin_branch_src2_2;
  reg        [19:0]   _zz_execute_BranchPlugin_branch_src2_3;
  wire                _zz_execute_BranchPlugin_branch_src2_4;
  reg        [18:0]   _zz_execute_BranchPlugin_branch_src2_5;
  reg        [31:0]   _zz_execute_BranchPlugin_branch_src2_6;
  wire       [31:0]   execute_BranchPlugin_branch_src2;
  wire       [31:0]   execute_BranchPlugin_branchAdder;
  wire                when_Pipeline_l124;
  reg        [31:0]   decode_to_execute_PC;
  wire                when_Pipeline_l124_1;
  reg        [31:0]   execute_to_memory_PC;
  wire                when_Pipeline_l124_2;
  reg        [31:0]   memory_to_writeBack_PC;
  wire                when_Pipeline_l124_3;
  reg        [31:0]   decode_to_execute_INSTRUCTION;
  wire                when_Pipeline_l124_4;
  reg        [31:0]   execute_to_memory_INSTRUCTION;
  wire                when_Pipeline_l124_5;
  reg        [31:0]   memory_to_writeBack_INSTRUCTION;
  wire                when_Pipeline_l124_6;
  reg        [31:0]   decode_to_execute_FORMAL_PC_NEXT;
  wire                when_Pipeline_l124_7;
  reg        [31:0]   execute_to_memory_FORMAL_PC_NEXT;
  wire                when_Pipeline_l124_8;
  reg        [31:0]   memory_to_writeBack_FORMAL_PC_NEXT;
  wire                when_Pipeline_l124_9;
  reg                 decode_to_execute_CSR_WRITE_OPCODE;
  wire                when_Pipeline_l124_10;
  reg                 decode_to_execute_CSR_READ_OPCODE;
  wire                when_Pipeline_l124_11;
  reg                 decode_to_execute_SRC_USE_SUB_LESS;
  wire                when_Pipeline_l124_12;
  reg                 decode_to_execute_MEMORY_ENABLE;
  wire                when_Pipeline_l124_13;
  reg                 execute_to_memory_MEMORY_ENABLE;
  wire                when_Pipeline_l124_14;
  reg                 memory_to_writeBack_MEMORY_ENABLE;
  wire                when_Pipeline_l124_15;
  reg                 decode_to_execute_REGFILE_WRITE_VALID;
  wire                when_Pipeline_l124_16;
  reg                 execute_to_memory_REGFILE_WRITE_VALID;
  wire                when_Pipeline_l124_17;
  reg                 memory_to_writeBack_REGFILE_WRITE_VALID;
  wire                when_Pipeline_l124_18;
  reg                 decode_to_execute_BYPASSABLE_EXECUTE_STAGE;
  wire                when_Pipeline_l124_19;
  reg                 decode_to_execute_BYPASSABLE_MEMORY_STAGE;
  wire                when_Pipeline_l124_20;
  reg                 execute_to_memory_BYPASSABLE_MEMORY_STAGE;
  wire                when_Pipeline_l124_21;
  reg                 decode_to_execute_MEMORY_STORE;
  wire                when_Pipeline_l124_22;
  reg                 execute_to_memory_MEMORY_STORE;
  wire                when_Pipeline_l124_23;
  reg                 decode_to_execute_IS_CSR;
  wire                when_Pipeline_l124_24;
  reg        [0:0]    decode_to_execute_ENV_CTRL;
  wire                when_Pipeline_l124_25;
  reg        [0:0]    execute_to_memory_ENV_CTRL;
  wire                when_Pipeline_l124_26;
  reg        [0:0]    memory_to_writeBack_ENV_CTRL;
  wire                when_Pipeline_l124_27;
  reg        [1:0]    decode_to_execute_ALU_CTRL;
  wire                when_Pipeline_l124_28;
  reg                 decode_to_execute_SRC_LESS_UNSIGNED;
  wire                when_Pipeline_l124_29;
  reg        [1:0]    decode_to_execute_ALU_BITWISE_CTRL;
  wire                when_Pipeline_l124_30;
  reg        [1:0]    decode_to_execute_SHIFT_CTRL;
  wire                when_Pipeline_l124_31;
  reg        [1:0]    decode_to_execute_BRANCH_CTRL;
  wire                when_Pipeline_l124_32;
  reg        [31:0]   decode_to_execute_RS1;
  wire                when_Pipeline_l124_33;
  reg        [31:0]   decode_to_execute_RS2;
  wire                when_Pipeline_l124_34;
  reg                 decode_to_execute_SRC2_FORCE_ZERO;
  wire                when_Pipeline_l124_35;
  reg        [31:0]   decode_to_execute_SRC1;
  wire                when_Pipeline_l124_36;
  reg        [31:0]   decode_to_execute_SRC2;
  wire                when_Pipeline_l124_37;
  reg        [1:0]    execute_to_memory_MEMORY_ADDRESS_LOW;
  wire                when_Pipeline_l124_38;
  reg        [1:0]    memory_to_writeBack_MEMORY_ADDRESS_LOW;
  wire                when_Pipeline_l124_39;
  reg        [31:0]   execute_to_memory_REGFILE_WRITE_DATA;
  wire                when_Pipeline_l124_40;
  reg        [31:0]   memory_to_writeBack_REGFILE_WRITE_DATA;
  wire                when_Pipeline_l124_41;
  reg                 execute_to_memory_BRANCH_DO;
  wire                when_Pipeline_l124_42;
  reg        [31:0]   execute_to_memory_BRANCH_CALC;
  wire                when_Pipeline_l124_43;
  reg        [31:0]   memory_to_writeBack_MEMORY_READ_DATA;
  wire                when_Pipeline_l151;
  wire                when_Pipeline_l154;
  wire                when_Pipeline_l151_1;
  wire                when_Pipeline_l154_1;
  wire                when_Pipeline_l151_2;
  wire                when_Pipeline_l154_2;
  wire                when_CsrPlugin_l1591;
  reg                 execute_CsrPlugin_csr_768;
  wire                when_CsrPlugin_l1591_1;
  reg                 execute_CsrPlugin_csr_836;
  wire                when_CsrPlugin_l1591_2;
  reg                 execute_CsrPlugin_csr_772;
  wire                when_CsrPlugin_l1591_3;
  reg                 execute_CsrPlugin_csr_834;
  wire       [1:0]    switch_CsrPlugin_l982;
  reg        [31:0]   _zz_CsrPlugin_csrMapping_readDataInit;
  reg        [31:0]   _zz_CsrPlugin_csrMapping_readDataInit_1;
  reg        [31:0]   _zz_CsrPlugin_csrMapping_readDataInit_2;
  reg        [31:0]   _zz_CsrPlugin_csrMapping_readDataInit_3;
  reg                 when_CsrPlugin_l1627;
  wire                when_CsrPlugin_l1625;
  wire                when_CsrPlugin_l1633;
  `ifndef SYNTHESIS
  reg [31:0] decode_BRANCH_CTRL_string;
  reg [31:0] _zz_decode_BRANCH_CTRL_string;
  reg [31:0] _zz_decode_to_execute_BRANCH_CTRL_string;
  reg [31:0] _zz_decode_to_execute_BRANCH_CTRL_1_string;
  reg [71:0] decode_SHIFT_CTRL_string;
  reg [71:0] _zz_decode_SHIFT_CTRL_string;
  reg [71:0] _zz_decode_to_execute_SHIFT_CTRL_string;
  reg [71:0] _zz_decode_to_execute_SHIFT_CTRL_1_string;
  reg [39:0] decode_ALU_BITWISE_CTRL_string;
  reg [39:0] _zz_decode_ALU_BITWISE_CTRL_string;
  reg [39:0] _zz_decode_to_execute_ALU_BITWISE_CTRL_string;
  reg [39:0] _zz_decode_to_execute_ALU_BITWISE_CTRL_1_string;
  reg [63:0] decode_ALU_CTRL_string;
  reg [63:0] _zz_decode_ALU_CTRL_string;
  reg [63:0] _zz_decode_to_execute_ALU_CTRL_string;
  reg [63:0] _zz_decode_to_execute_ALU_CTRL_1_string;
  reg [31:0] _zz_memory_to_writeBack_ENV_CTRL_string;
  reg [31:0] _zz_memory_to_writeBack_ENV_CTRL_1_string;
  reg [31:0] _zz_execute_to_memory_ENV_CTRL_string;
  reg [31:0] _zz_execute_to_memory_ENV_CTRL_1_string;
  reg [31:0] decode_ENV_CTRL_string;
  reg [31:0] _zz_decode_ENV_CTRL_string;
  reg [31:0] _zz_decode_to_execute_ENV_CTRL_string;
  reg [31:0] _zz_decode_to_execute_ENV_CTRL_1_string;
  reg [31:0] execute_BRANCH_CTRL_string;
  reg [31:0] _zz_execute_BRANCH_CTRL_string;
  reg [71:0] execute_SHIFT_CTRL_string;
  reg [71:0] _zz_execute_SHIFT_CTRL_string;
  reg [23:0] decode_SRC2_CTRL_string;
  reg [23:0] _zz_decode_SRC2_CTRL_string;
  reg [95:0] decode_SRC1_CTRL_string;
  reg [95:0] _zz_decode_SRC1_CTRL_string;
  reg [63:0] execute_ALU_CTRL_string;
  reg [63:0] _zz_execute_ALU_CTRL_string;
  reg [39:0] execute_ALU_BITWISE_CTRL_string;
  reg [39:0] _zz_execute_ALU_BITWISE_CTRL_string;
  reg [31:0] _zz_decode_BRANCH_CTRL_1_string;
  reg [71:0] _zz_decode_SHIFT_CTRL_1_string;
  reg [39:0] _zz_decode_ALU_BITWISE_CTRL_1_string;
  reg [63:0] _zz_decode_ALU_CTRL_1_string;
  reg [31:0] _zz_decode_ENV_CTRL_1_string;
  reg [23:0] _zz_decode_SRC2_CTRL_1_string;
  reg [95:0] _zz_decode_SRC1_CTRL_1_string;
  reg [31:0] memory_ENV_CTRL_string;
  reg [31:0] _zz_memory_ENV_CTRL_string;
  reg [31:0] execute_ENV_CTRL_string;
  reg [31:0] _zz_execute_ENV_CTRL_string;
  reg [31:0] writeBack_ENV_CTRL_string;
  reg [31:0] _zz_writeBack_ENV_CTRL_string;
  reg [95:0] _zz_decode_SRC1_CTRL_2_string;
  reg [23:0] _zz_decode_SRC2_CTRL_2_string;
  reg [31:0] _zz_decode_ENV_CTRL_2_string;
  reg [63:0] _zz_decode_ALU_CTRL_2_string;
  reg [39:0] _zz_decode_ALU_BITWISE_CTRL_2_string;
  reg [71:0] _zz_decode_SHIFT_CTRL_2_string;
  reg [31:0] _zz_decode_BRANCH_CTRL_2_string;
  reg [31:0] decode_to_execute_ENV_CTRL_string;
  reg [31:0] execute_to_memory_ENV_CTRL_string;
  reg [31:0] memory_to_writeBack_ENV_CTRL_string;
  reg [63:0] decode_to_execute_ALU_CTRL_string;
  reg [39:0] decode_to_execute_ALU_BITWISE_CTRL_string;
  reg [71:0] decode_to_execute_SHIFT_CTRL_string;
  reg [31:0] decode_to_execute_BRANCH_CTRL_string;
  `endif

  reg [31:0] RegFilePlugin_regFile [0:31] /* verilator public */ ;

  assign _zz_IBusSimplePlugin_jump_pcLoad_payload_1 = (_zz_IBusSimplePlugin_jump_pcLoad_payload & (~ _zz_IBusSimplePlugin_jump_pcLoad_payload_2));
  assign _zz_IBusSimplePlugin_jump_pcLoad_payload_2 = (_zz_IBusSimplePlugin_jump_pcLoad_payload - 2'b01);
  assign _zz_IBusSimplePlugin_fetchPc_pc_1 = {IBusSimplePlugin_fetchPc_inc,2'b00};
  assign _zz_IBusSimplePlugin_fetchPc_pc = {29'd0, _zz_IBusSimplePlugin_fetchPc_pc_1};
  assign _zz_IBusSimplePlugin_pending_next = (IBusSimplePlugin_pending_value + IBusSimplePlugin_pending_inc);
  assign _zz__zz_execute_REGFILE_WRITE_DATA = execute_SRC_LESS;
  assign _zz__zz_decode_SRC1 = 3'b100;
  assign _zz__zz_decode_SRC1_1 = decode_INSTRUCTION[19 : 15];
  assign _zz__zz_decode_SRC2_2 = {decode_INSTRUCTION[31 : 25],decode_INSTRUCTION[11 : 7]};
  assign _zz_execute_SrcPlugin_addSub = ($signed(_zz_execute_SrcPlugin_addSub_1) + $signed(_zz_execute_SrcPlugin_addSub_4));
  assign _zz_execute_SrcPlugin_addSub_1 = ($signed(_zz_execute_SrcPlugin_addSub_2) + $signed(_zz_execute_SrcPlugin_addSub_3));
  assign _zz_execute_SrcPlugin_addSub_2 = execute_SRC1;
  assign _zz_execute_SrcPlugin_addSub_3 = (execute_SRC_USE_SUB_LESS ? (~ execute_SRC2) : execute_SRC2);
  assign _zz_execute_SrcPlugin_addSub_4 = (execute_SRC_USE_SUB_LESS ? _zz_execute_SrcPlugin_addSub_5 : _zz_execute_SrcPlugin_addSub_6);
  assign _zz_execute_SrcPlugin_addSub_5 = 32'h00000001;
  assign _zz_execute_SrcPlugin_addSub_6 = 32'h0;
  assign _zz__zz_execute_to_memory_REGFILE_WRITE_DATA_1 = (_zz__zz_execute_to_memory_REGFILE_WRITE_DATA_1_1 >>> 1);
  assign _zz__zz_execute_to_memory_REGFILE_WRITE_DATA_1_1 = {((execute_SHIFT_CTRL == ShiftCtrlEnum_SRA_1) && execute_LightShifterPlugin_shiftInput[31]),execute_LightShifterPlugin_shiftInput};
  assign _zz__zz_execute_BranchPlugin_branch_src2 = {{{execute_INSTRUCTION[31],execute_INSTRUCTION[19 : 12]},execute_INSTRUCTION[20]},execute_INSTRUCTION[30 : 21]};
  assign _zz__zz_execute_BranchPlugin_branch_src2_4 = {{{execute_INSTRUCTION[31],execute_INSTRUCTION[7]},execute_INSTRUCTION[30 : 25]},execute_INSTRUCTION[11 : 8]};
  assign _zz_decode_RegFilePlugin_rs1Data = 1'b1;
  assign _zz_decode_RegFilePlugin_rs2Data = 1'b1;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED = (decode_INSTRUCTION & 32'h0000001c);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_1 = 32'h00000004;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_2 = (decode_INSTRUCTION & 32'h00000058);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_3 = 32'h00000040;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_4 = ((decode_INSTRUCTION & 32'h00007054) == 32'h00005010);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_5 = {((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_6) == 32'h40001010),((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_7) == 32'h00001010)};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_8 = (|{(_zz__zz_decode_SRC_LESS_UNSIGNED_9 == _zz__zz_decode_SRC_LESS_UNSIGNED_10),(_zz__zz_decode_SRC_LESS_UNSIGNED_11 == _zz__zz_decode_SRC_LESS_UNSIGNED_12)});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_13 = (|(_zz__zz_decode_SRC_LESS_UNSIGNED_14 == _zz__zz_decode_SRC_LESS_UNSIGNED_15));
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_16 = {(|_zz__zz_decode_SRC_LESS_UNSIGNED_17),{(|_zz__zz_decode_SRC_LESS_UNSIGNED_18),{_zz__zz_decode_SRC_LESS_UNSIGNED_21,{_zz__zz_decode_SRC_LESS_UNSIGNED_26,_zz__zz_decode_SRC_LESS_UNSIGNED_27}}}};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_6 = 32'h40003054;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_7 = 32'h00007054;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_9 = (decode_INSTRUCTION & 32'h00000064);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_10 = 32'h00000024;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_11 = (decode_INSTRUCTION & 32'h00003054);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_12 = 32'h00001010;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_14 = (decode_INSTRUCTION & 32'h00001000);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_15 = 32'h00001000;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_17 = ((decode_INSTRUCTION & 32'h00003000) == 32'h00002000);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_18 = {((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_19) == 32'h00001000),((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_20) == 32'h00002000)};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_21 = (|{(_zz__zz_decode_SRC_LESS_UNSIGNED_22 == _zz__zz_decode_SRC_LESS_UNSIGNED_23),(_zz__zz_decode_SRC_LESS_UNSIGNED_24 == _zz__zz_decode_SRC_LESS_UNSIGNED_25)});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_26 = (|_zz_decode_SRC_LESS_UNSIGNED_2);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_27 = {(|_zz__zz_decode_SRC_LESS_UNSIGNED_28),{(|_zz__zz_decode_SRC_LESS_UNSIGNED_29),{_zz__zz_decode_SRC_LESS_UNSIGNED_32,{_zz__zz_decode_SRC_LESS_UNSIGNED_37,_zz__zz_decode_SRC_LESS_UNSIGNED_42}}}};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_19 = 32'h00005000;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_20 = 32'h00002010;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_22 = (decode_INSTRUCTION & 32'h00006004);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_23 = 32'h00006000;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_24 = (decode_INSTRUCTION & 32'h00005004);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_25 = 32'h00004000;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_28 = ((decode_INSTRUCTION & 32'h00003050) == 32'h00000050);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_29 = {((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_30) == 32'h00001050),((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_31) == 32'h00002050)};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_32 = (|{(_zz__zz_decode_SRC_LESS_UNSIGNED_33 == _zz__zz_decode_SRC_LESS_UNSIGNED_34),(_zz__zz_decode_SRC_LESS_UNSIGNED_35 == _zz__zz_decode_SRC_LESS_UNSIGNED_36)});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_37 = (|{_zz__zz_decode_SRC_LESS_UNSIGNED_38,{_zz__zz_decode_SRC_LESS_UNSIGNED_39,_zz__zz_decode_SRC_LESS_UNSIGNED_40}});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_42 = {(|_zz__zz_decode_SRC_LESS_UNSIGNED_43),{(|_zz__zz_decode_SRC_LESS_UNSIGNED_44),{_zz__zz_decode_SRC_LESS_UNSIGNED_46,{_zz__zz_decode_SRC_LESS_UNSIGNED_49,_zz__zz_decode_SRC_LESS_UNSIGNED_62}}}};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_30 = 32'h00001050;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_31 = 32'h00002050;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_33 = (decode_INSTRUCTION & 32'h00000054);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_34 = 32'h00000040;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_35 = (decode_INSTRUCTION & 32'h00000064);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_36 = 32'h00000020;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_38 = ((decode_INSTRUCTION & 32'h00000050) == 32'h00000040);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_39 = _zz_decode_SRC_LESS_UNSIGNED_3;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_40 = ((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_41) == 32'h00000040);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_43 = ((decode_INSTRUCTION & 32'h00000020) == 32'h00000020);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_44 = ((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_45) == 32'h00000010);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_46 = (|(_zz__zz_decode_SRC_LESS_UNSIGNED_47 == _zz__zz_decode_SRC_LESS_UNSIGNED_48));
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_49 = (|{_zz__zz_decode_SRC_LESS_UNSIGNED_50,_zz__zz_decode_SRC_LESS_UNSIGNED_52});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_62 = {(|_zz__zz_decode_SRC_LESS_UNSIGNED_63),{_zz__zz_decode_SRC_LESS_UNSIGNED_66,{_zz__zz_decode_SRC_LESS_UNSIGNED_68,_zz__zz_decode_SRC_LESS_UNSIGNED_73}}};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_41 = 32'h00003040;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_45 = 32'h00000010;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_47 = (decode_INSTRUCTION & 32'h00000050);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_48 = 32'h00000010;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_50 = ((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_51) == 32'h00001010);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_52 = {(_zz__zz_decode_SRC_LESS_UNSIGNED_53 == _zz__zz_decode_SRC_LESS_UNSIGNED_54),{_zz_decode_SRC_LESS_UNSIGNED_5,{_zz__zz_decode_SRC_LESS_UNSIGNED_55,_zz__zz_decode_SRC_LESS_UNSIGNED_57}}};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_63 = {_zz_decode_SRC_LESS_UNSIGNED_4,(_zz__zz_decode_SRC_LESS_UNSIGNED_64 == _zz__zz_decode_SRC_LESS_UNSIGNED_65)};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_66 = (|{_zz_decode_SRC_LESS_UNSIGNED_4,_zz__zz_decode_SRC_LESS_UNSIGNED_67});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_68 = (|{_zz__zz_decode_SRC_LESS_UNSIGNED_69,_zz__zz_decode_SRC_LESS_UNSIGNED_71});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_73 = {(|_zz__zz_decode_SRC_LESS_UNSIGNED_74),{_zz__zz_decode_SRC_LESS_UNSIGNED_76,{_zz__zz_decode_SRC_LESS_UNSIGNED_80,_zz__zz_decode_SRC_LESS_UNSIGNED_83}}};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_51 = 32'h00001010;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_53 = (decode_INSTRUCTION & 32'h00002010);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_54 = 32'h00002010;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_55 = ((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_56) == 32'h00000010);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_57 = {(_zz__zz_decode_SRC_LESS_UNSIGNED_58 == _zz__zz_decode_SRC_LESS_UNSIGNED_59),(_zz__zz_decode_SRC_LESS_UNSIGNED_60 == _zz__zz_decode_SRC_LESS_UNSIGNED_61)};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_64 = (decode_INSTRUCTION & 32'h00000070);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_65 = 32'h00000020;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_67 = ((decode_INSTRUCTION & 32'h00000020) == 32'h0);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_69 = ((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_70) == 32'h0);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_71 = {_zz_decode_SRC_LESS_UNSIGNED_3,{_zz_decode_SRC_LESS_UNSIGNED_2,_zz__zz_decode_SRC_LESS_UNSIGNED_72}};
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_74 = ((decode_INSTRUCTION & _zz__zz_decode_SRC_LESS_UNSIGNED_75) == 32'h0);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_76 = (|{_zz__zz_decode_SRC_LESS_UNSIGNED_77,{_zz__zz_decode_SRC_LESS_UNSIGNED_78,_zz__zz_decode_SRC_LESS_UNSIGNED_79}});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_80 = (|{_zz__zz_decode_SRC_LESS_UNSIGNED_81,_zz__zz_decode_SRC_LESS_UNSIGNED_82});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_83 = (|{_zz__zz_decode_SRC_LESS_UNSIGNED_84,_zz__zz_decode_SRC_LESS_UNSIGNED_85});
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_56 = 32'h10000010;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_58 = (decode_INSTRUCTION & 32'h0000000c);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_59 = 32'h00000004;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_60 = (decode_INSTRUCTION & 32'h00000028);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_61 = 32'h0;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_70 = 32'h00000044;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_72 = ((decode_INSTRUCTION & 32'h00005004) == 32'h00001000);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_75 = 32'h00000058;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_77 = ((decode_INSTRUCTION & 32'h00000044) == 32'h00000040);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_78 = ((decode_INSTRUCTION & 32'h00002014) == 32'h00002010);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_79 = ((decode_INSTRUCTION & 32'h40004034) == 32'h40000030);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_81 = ((decode_INSTRUCTION & 32'h00000014) == 32'h00000004);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_82 = _zz_decode_SRC_LESS_UNSIGNED_1;
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_84 = ((decode_INSTRUCTION & 32'h00000044) == 32'h00000004);
  assign _zz__zz_decode_SRC_LESS_UNSIGNED_85 = _zz_decode_SRC_LESS_UNSIGNED_1;
  always @(posedge clk) begin
    if(_zz_decode_RegFilePlugin_rs1Data) begin
      _zz_RegFilePlugin_regFile_port0 <= RegFilePlugin_regFile[decode_RegFilePlugin_regFileReadAddress1];
    end
  end

  always @(posedge clk) begin
    if(_zz_decode_RegFilePlugin_rs2Data) begin
      _zz_RegFilePlugin_regFile_port1 <= RegFilePlugin_regFile[decode_RegFilePlugin_regFileReadAddress2];
    end
  end

  always @(posedge clk) begin
    if(_zz_1) begin
      RegFilePlugin_regFile[lastStageRegFileWrite_payload_address] <= lastStageRegFileWrite_payload_data;
    end
  end

  StreamFifoLowLatency IBusSimplePlugin_rspJoin_rspBuffer_c (
    .io_push_valid         (iBus_rsp_toStream_valid                                       ), //i
    .io_push_ready         (IBusSimplePlugin_rspJoin_rspBuffer_c_io_push_ready            ), //o
    .io_push_payload_error (iBus_rsp_toStream_payload_error                               ), //i
    .io_push_payload_inst  (iBus_rsp_toStream_payload_inst[31:0]                          ), //i
    .io_pop_valid          (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid             ), //o
    .io_pop_ready          (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_ready             ), //i
    .io_pop_payload_error  (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_error     ), //o
    .io_pop_payload_inst   (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_inst[31:0]), //o
    .io_flush              (1'b0                                                          ), //i
    .io_occupancy          (IBusSimplePlugin_rspJoin_rspBuffer_c_io_occupancy             ), //o
    .clk                   (clk                                                           ), //i
    .reset                 (reset                                                         )  //i
  );
  `ifndef SYNTHESIS
  always @(*) begin
    case(decode_BRANCH_CTRL)
      BranchCtrlEnum_INC : decode_BRANCH_CTRL_string = "INC ";
      BranchCtrlEnum_B : decode_BRANCH_CTRL_string = "B   ";
      BranchCtrlEnum_JAL : decode_BRANCH_CTRL_string = "JAL ";
      BranchCtrlEnum_JALR : decode_BRANCH_CTRL_string = "JALR";
      default : decode_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_BRANCH_CTRL)
      BranchCtrlEnum_INC : _zz_decode_BRANCH_CTRL_string = "INC ";
      BranchCtrlEnum_B : _zz_decode_BRANCH_CTRL_string = "B   ";
      BranchCtrlEnum_JAL : _zz_decode_BRANCH_CTRL_string = "JAL ";
      BranchCtrlEnum_JALR : _zz_decode_BRANCH_CTRL_string = "JALR";
      default : _zz_decode_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_BRANCH_CTRL)
      BranchCtrlEnum_INC : _zz_decode_to_execute_BRANCH_CTRL_string = "INC ";
      BranchCtrlEnum_B : _zz_decode_to_execute_BRANCH_CTRL_string = "B   ";
      BranchCtrlEnum_JAL : _zz_decode_to_execute_BRANCH_CTRL_string = "JAL ";
      BranchCtrlEnum_JALR : _zz_decode_to_execute_BRANCH_CTRL_string = "JALR";
      default : _zz_decode_to_execute_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_BRANCH_CTRL_1)
      BranchCtrlEnum_INC : _zz_decode_to_execute_BRANCH_CTRL_1_string = "INC ";
      BranchCtrlEnum_B : _zz_decode_to_execute_BRANCH_CTRL_1_string = "B   ";
      BranchCtrlEnum_JAL : _zz_decode_to_execute_BRANCH_CTRL_1_string = "JAL ";
      BranchCtrlEnum_JALR : _zz_decode_to_execute_BRANCH_CTRL_1_string = "JALR";
      default : _zz_decode_to_execute_BRANCH_CTRL_1_string = "????";
    endcase
  end
  always @(*) begin
    case(decode_SHIFT_CTRL)
      ShiftCtrlEnum_DISABLE_1 : decode_SHIFT_CTRL_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : decode_SHIFT_CTRL_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : decode_SHIFT_CTRL_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : decode_SHIFT_CTRL_string = "SRA_1    ";
      default : decode_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SHIFT_CTRL)
      ShiftCtrlEnum_DISABLE_1 : _zz_decode_SHIFT_CTRL_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : _zz_decode_SHIFT_CTRL_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : _zz_decode_SHIFT_CTRL_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : _zz_decode_SHIFT_CTRL_string = "SRA_1    ";
      default : _zz_decode_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_SHIFT_CTRL)
      ShiftCtrlEnum_DISABLE_1 : _zz_decode_to_execute_SHIFT_CTRL_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : _zz_decode_to_execute_SHIFT_CTRL_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : _zz_decode_to_execute_SHIFT_CTRL_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : _zz_decode_to_execute_SHIFT_CTRL_string = "SRA_1    ";
      default : _zz_decode_to_execute_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_SHIFT_CTRL_1)
      ShiftCtrlEnum_DISABLE_1 : _zz_decode_to_execute_SHIFT_CTRL_1_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : _zz_decode_to_execute_SHIFT_CTRL_1_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : _zz_decode_to_execute_SHIFT_CTRL_1_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : _zz_decode_to_execute_SHIFT_CTRL_1_string = "SRA_1    ";
      default : _zz_decode_to_execute_SHIFT_CTRL_1_string = "?????????";
    endcase
  end
  always @(*) begin
    case(decode_ALU_BITWISE_CTRL)
      AluBitwiseCtrlEnum_XOR_1 : decode_ALU_BITWISE_CTRL_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : decode_ALU_BITWISE_CTRL_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : decode_ALU_BITWISE_CTRL_string = "AND_1";
      default : decode_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ALU_BITWISE_CTRL)
      AluBitwiseCtrlEnum_XOR_1 : _zz_decode_ALU_BITWISE_CTRL_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : _zz_decode_ALU_BITWISE_CTRL_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : _zz_decode_ALU_BITWISE_CTRL_string = "AND_1";
      default : _zz_decode_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_ALU_BITWISE_CTRL)
      AluBitwiseCtrlEnum_XOR_1 : _zz_decode_to_execute_ALU_BITWISE_CTRL_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : _zz_decode_to_execute_ALU_BITWISE_CTRL_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : _zz_decode_to_execute_ALU_BITWISE_CTRL_string = "AND_1";
      default : _zz_decode_to_execute_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_ALU_BITWISE_CTRL_1)
      AluBitwiseCtrlEnum_XOR_1 : _zz_decode_to_execute_ALU_BITWISE_CTRL_1_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : _zz_decode_to_execute_ALU_BITWISE_CTRL_1_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : _zz_decode_to_execute_ALU_BITWISE_CTRL_1_string = "AND_1";
      default : _zz_decode_to_execute_ALU_BITWISE_CTRL_1_string = "?????";
    endcase
  end
  always @(*) begin
    case(decode_ALU_CTRL)
      AluCtrlEnum_ADD_SUB : decode_ALU_CTRL_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : decode_ALU_CTRL_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : decode_ALU_CTRL_string = "BITWISE ";
      default : decode_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ALU_CTRL)
      AluCtrlEnum_ADD_SUB : _zz_decode_ALU_CTRL_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : _zz_decode_ALU_CTRL_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : _zz_decode_ALU_CTRL_string = "BITWISE ";
      default : _zz_decode_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_ALU_CTRL)
      AluCtrlEnum_ADD_SUB : _zz_decode_to_execute_ALU_CTRL_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : _zz_decode_to_execute_ALU_CTRL_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : _zz_decode_to_execute_ALU_CTRL_string = "BITWISE ";
      default : _zz_decode_to_execute_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_ALU_CTRL_1)
      AluCtrlEnum_ADD_SUB : _zz_decode_to_execute_ALU_CTRL_1_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : _zz_decode_to_execute_ALU_CTRL_1_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : _zz_decode_to_execute_ALU_CTRL_1_string = "BITWISE ";
      default : _zz_decode_to_execute_ALU_CTRL_1_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_memory_to_writeBack_ENV_CTRL)
      EnvCtrlEnum_NONE : _zz_memory_to_writeBack_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : _zz_memory_to_writeBack_ENV_CTRL_string = "XRET";
      default : _zz_memory_to_writeBack_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_memory_to_writeBack_ENV_CTRL_1)
      EnvCtrlEnum_NONE : _zz_memory_to_writeBack_ENV_CTRL_1_string = "NONE";
      EnvCtrlEnum_XRET : _zz_memory_to_writeBack_ENV_CTRL_1_string = "XRET";
      default : _zz_memory_to_writeBack_ENV_CTRL_1_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_execute_to_memory_ENV_CTRL)
      EnvCtrlEnum_NONE : _zz_execute_to_memory_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : _zz_execute_to_memory_ENV_CTRL_string = "XRET";
      default : _zz_execute_to_memory_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_execute_to_memory_ENV_CTRL_1)
      EnvCtrlEnum_NONE : _zz_execute_to_memory_ENV_CTRL_1_string = "NONE";
      EnvCtrlEnum_XRET : _zz_execute_to_memory_ENV_CTRL_1_string = "XRET";
      default : _zz_execute_to_memory_ENV_CTRL_1_string = "????";
    endcase
  end
  always @(*) begin
    case(decode_ENV_CTRL)
      EnvCtrlEnum_NONE : decode_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : decode_ENV_CTRL_string = "XRET";
      default : decode_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ENV_CTRL)
      EnvCtrlEnum_NONE : _zz_decode_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : _zz_decode_ENV_CTRL_string = "XRET";
      default : _zz_decode_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_ENV_CTRL)
      EnvCtrlEnum_NONE : _zz_decode_to_execute_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : _zz_decode_to_execute_ENV_CTRL_string = "XRET";
      default : _zz_decode_to_execute_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_to_execute_ENV_CTRL_1)
      EnvCtrlEnum_NONE : _zz_decode_to_execute_ENV_CTRL_1_string = "NONE";
      EnvCtrlEnum_XRET : _zz_decode_to_execute_ENV_CTRL_1_string = "XRET";
      default : _zz_decode_to_execute_ENV_CTRL_1_string = "????";
    endcase
  end
  always @(*) begin
    case(execute_BRANCH_CTRL)
      BranchCtrlEnum_INC : execute_BRANCH_CTRL_string = "INC ";
      BranchCtrlEnum_B : execute_BRANCH_CTRL_string = "B   ";
      BranchCtrlEnum_JAL : execute_BRANCH_CTRL_string = "JAL ";
      BranchCtrlEnum_JALR : execute_BRANCH_CTRL_string = "JALR";
      default : execute_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_execute_BRANCH_CTRL)
      BranchCtrlEnum_INC : _zz_execute_BRANCH_CTRL_string = "INC ";
      BranchCtrlEnum_B : _zz_execute_BRANCH_CTRL_string = "B   ";
      BranchCtrlEnum_JAL : _zz_execute_BRANCH_CTRL_string = "JAL ";
      BranchCtrlEnum_JALR : _zz_execute_BRANCH_CTRL_string = "JALR";
      default : _zz_execute_BRANCH_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(execute_SHIFT_CTRL)
      ShiftCtrlEnum_DISABLE_1 : execute_SHIFT_CTRL_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : execute_SHIFT_CTRL_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : execute_SHIFT_CTRL_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : execute_SHIFT_CTRL_string = "SRA_1    ";
      default : execute_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_execute_SHIFT_CTRL)
      ShiftCtrlEnum_DISABLE_1 : _zz_execute_SHIFT_CTRL_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : _zz_execute_SHIFT_CTRL_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : _zz_execute_SHIFT_CTRL_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : _zz_execute_SHIFT_CTRL_string = "SRA_1    ";
      default : _zz_execute_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(decode_SRC2_CTRL)
      Src2CtrlEnum_RS : decode_SRC2_CTRL_string = "RS ";
      Src2CtrlEnum_IMI : decode_SRC2_CTRL_string = "IMI";
      Src2CtrlEnum_IMS : decode_SRC2_CTRL_string = "IMS";
      Src2CtrlEnum_PC : decode_SRC2_CTRL_string = "PC ";
      default : decode_SRC2_CTRL_string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SRC2_CTRL)
      Src2CtrlEnum_RS : _zz_decode_SRC2_CTRL_string = "RS ";
      Src2CtrlEnum_IMI : _zz_decode_SRC2_CTRL_string = "IMI";
      Src2CtrlEnum_IMS : _zz_decode_SRC2_CTRL_string = "IMS";
      Src2CtrlEnum_PC : _zz_decode_SRC2_CTRL_string = "PC ";
      default : _zz_decode_SRC2_CTRL_string = "???";
    endcase
  end
  always @(*) begin
    case(decode_SRC1_CTRL)
      Src1CtrlEnum_RS : decode_SRC1_CTRL_string = "RS          ";
      Src1CtrlEnum_IMU : decode_SRC1_CTRL_string = "IMU         ";
      Src1CtrlEnum_PC_INCREMENT : decode_SRC1_CTRL_string = "PC_INCREMENT";
      Src1CtrlEnum_URS1 : decode_SRC1_CTRL_string = "URS1        ";
      default : decode_SRC1_CTRL_string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SRC1_CTRL)
      Src1CtrlEnum_RS : _zz_decode_SRC1_CTRL_string = "RS          ";
      Src1CtrlEnum_IMU : _zz_decode_SRC1_CTRL_string = "IMU         ";
      Src1CtrlEnum_PC_INCREMENT : _zz_decode_SRC1_CTRL_string = "PC_INCREMENT";
      Src1CtrlEnum_URS1 : _zz_decode_SRC1_CTRL_string = "URS1        ";
      default : _zz_decode_SRC1_CTRL_string = "????????????";
    endcase
  end
  always @(*) begin
    case(execute_ALU_CTRL)
      AluCtrlEnum_ADD_SUB : execute_ALU_CTRL_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : execute_ALU_CTRL_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : execute_ALU_CTRL_string = "BITWISE ";
      default : execute_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_execute_ALU_CTRL)
      AluCtrlEnum_ADD_SUB : _zz_execute_ALU_CTRL_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : _zz_execute_ALU_CTRL_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : _zz_execute_ALU_CTRL_string = "BITWISE ";
      default : _zz_execute_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(execute_ALU_BITWISE_CTRL)
      AluBitwiseCtrlEnum_XOR_1 : execute_ALU_BITWISE_CTRL_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : execute_ALU_BITWISE_CTRL_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : execute_ALU_BITWISE_CTRL_string = "AND_1";
      default : execute_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_execute_ALU_BITWISE_CTRL)
      AluBitwiseCtrlEnum_XOR_1 : _zz_execute_ALU_BITWISE_CTRL_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : _zz_execute_ALU_BITWISE_CTRL_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : _zz_execute_ALU_BITWISE_CTRL_string = "AND_1";
      default : _zz_execute_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_BRANCH_CTRL_1)
      BranchCtrlEnum_INC : _zz_decode_BRANCH_CTRL_1_string = "INC ";
      BranchCtrlEnum_B : _zz_decode_BRANCH_CTRL_1_string = "B   ";
      BranchCtrlEnum_JAL : _zz_decode_BRANCH_CTRL_1_string = "JAL ";
      BranchCtrlEnum_JALR : _zz_decode_BRANCH_CTRL_1_string = "JALR";
      default : _zz_decode_BRANCH_CTRL_1_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SHIFT_CTRL_1)
      ShiftCtrlEnum_DISABLE_1 : _zz_decode_SHIFT_CTRL_1_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : _zz_decode_SHIFT_CTRL_1_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : _zz_decode_SHIFT_CTRL_1_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : _zz_decode_SHIFT_CTRL_1_string = "SRA_1    ";
      default : _zz_decode_SHIFT_CTRL_1_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ALU_BITWISE_CTRL_1)
      AluBitwiseCtrlEnum_XOR_1 : _zz_decode_ALU_BITWISE_CTRL_1_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : _zz_decode_ALU_BITWISE_CTRL_1_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : _zz_decode_ALU_BITWISE_CTRL_1_string = "AND_1";
      default : _zz_decode_ALU_BITWISE_CTRL_1_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ALU_CTRL_1)
      AluCtrlEnum_ADD_SUB : _zz_decode_ALU_CTRL_1_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : _zz_decode_ALU_CTRL_1_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : _zz_decode_ALU_CTRL_1_string = "BITWISE ";
      default : _zz_decode_ALU_CTRL_1_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ENV_CTRL_1)
      EnvCtrlEnum_NONE : _zz_decode_ENV_CTRL_1_string = "NONE";
      EnvCtrlEnum_XRET : _zz_decode_ENV_CTRL_1_string = "XRET";
      default : _zz_decode_ENV_CTRL_1_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SRC2_CTRL_1)
      Src2CtrlEnum_RS : _zz_decode_SRC2_CTRL_1_string = "RS ";
      Src2CtrlEnum_IMI : _zz_decode_SRC2_CTRL_1_string = "IMI";
      Src2CtrlEnum_IMS : _zz_decode_SRC2_CTRL_1_string = "IMS";
      Src2CtrlEnum_PC : _zz_decode_SRC2_CTRL_1_string = "PC ";
      default : _zz_decode_SRC2_CTRL_1_string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SRC1_CTRL_1)
      Src1CtrlEnum_RS : _zz_decode_SRC1_CTRL_1_string = "RS          ";
      Src1CtrlEnum_IMU : _zz_decode_SRC1_CTRL_1_string = "IMU         ";
      Src1CtrlEnum_PC_INCREMENT : _zz_decode_SRC1_CTRL_1_string = "PC_INCREMENT";
      Src1CtrlEnum_URS1 : _zz_decode_SRC1_CTRL_1_string = "URS1        ";
      default : _zz_decode_SRC1_CTRL_1_string = "????????????";
    endcase
  end
  always @(*) begin
    case(memory_ENV_CTRL)
      EnvCtrlEnum_NONE : memory_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : memory_ENV_CTRL_string = "XRET";
      default : memory_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_memory_ENV_CTRL)
      EnvCtrlEnum_NONE : _zz_memory_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : _zz_memory_ENV_CTRL_string = "XRET";
      default : _zz_memory_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(execute_ENV_CTRL)
      EnvCtrlEnum_NONE : execute_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : execute_ENV_CTRL_string = "XRET";
      default : execute_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_execute_ENV_CTRL)
      EnvCtrlEnum_NONE : _zz_execute_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : _zz_execute_ENV_CTRL_string = "XRET";
      default : _zz_execute_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(writeBack_ENV_CTRL)
      EnvCtrlEnum_NONE : writeBack_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : writeBack_ENV_CTRL_string = "XRET";
      default : writeBack_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_writeBack_ENV_CTRL)
      EnvCtrlEnum_NONE : _zz_writeBack_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : _zz_writeBack_ENV_CTRL_string = "XRET";
      default : _zz_writeBack_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SRC1_CTRL_2)
      Src1CtrlEnum_RS : _zz_decode_SRC1_CTRL_2_string = "RS          ";
      Src1CtrlEnum_IMU : _zz_decode_SRC1_CTRL_2_string = "IMU         ";
      Src1CtrlEnum_PC_INCREMENT : _zz_decode_SRC1_CTRL_2_string = "PC_INCREMENT";
      Src1CtrlEnum_URS1 : _zz_decode_SRC1_CTRL_2_string = "URS1        ";
      default : _zz_decode_SRC1_CTRL_2_string = "????????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SRC2_CTRL_2)
      Src2CtrlEnum_RS : _zz_decode_SRC2_CTRL_2_string = "RS ";
      Src2CtrlEnum_IMI : _zz_decode_SRC2_CTRL_2_string = "IMI";
      Src2CtrlEnum_IMS : _zz_decode_SRC2_CTRL_2_string = "IMS";
      Src2CtrlEnum_PC : _zz_decode_SRC2_CTRL_2_string = "PC ";
      default : _zz_decode_SRC2_CTRL_2_string = "???";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ENV_CTRL_2)
      EnvCtrlEnum_NONE : _zz_decode_ENV_CTRL_2_string = "NONE";
      EnvCtrlEnum_XRET : _zz_decode_ENV_CTRL_2_string = "XRET";
      default : _zz_decode_ENV_CTRL_2_string = "????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ALU_CTRL_2)
      AluCtrlEnum_ADD_SUB : _zz_decode_ALU_CTRL_2_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : _zz_decode_ALU_CTRL_2_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : _zz_decode_ALU_CTRL_2_string = "BITWISE ";
      default : _zz_decode_ALU_CTRL_2_string = "????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_ALU_BITWISE_CTRL_2)
      AluBitwiseCtrlEnum_XOR_1 : _zz_decode_ALU_BITWISE_CTRL_2_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : _zz_decode_ALU_BITWISE_CTRL_2_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : _zz_decode_ALU_BITWISE_CTRL_2_string = "AND_1";
      default : _zz_decode_ALU_BITWISE_CTRL_2_string = "?????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_SHIFT_CTRL_2)
      ShiftCtrlEnum_DISABLE_1 : _zz_decode_SHIFT_CTRL_2_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : _zz_decode_SHIFT_CTRL_2_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : _zz_decode_SHIFT_CTRL_2_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : _zz_decode_SHIFT_CTRL_2_string = "SRA_1    ";
      default : _zz_decode_SHIFT_CTRL_2_string = "?????????";
    endcase
  end
  always @(*) begin
    case(_zz_decode_BRANCH_CTRL_2)
      BranchCtrlEnum_INC : _zz_decode_BRANCH_CTRL_2_string = "INC ";
      BranchCtrlEnum_B : _zz_decode_BRANCH_CTRL_2_string = "B   ";
      BranchCtrlEnum_JAL : _zz_decode_BRANCH_CTRL_2_string = "JAL ";
      BranchCtrlEnum_JALR : _zz_decode_BRANCH_CTRL_2_string = "JALR";
      default : _zz_decode_BRANCH_CTRL_2_string = "????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_ENV_CTRL)
      EnvCtrlEnum_NONE : decode_to_execute_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : decode_to_execute_ENV_CTRL_string = "XRET";
      default : decode_to_execute_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(execute_to_memory_ENV_CTRL)
      EnvCtrlEnum_NONE : execute_to_memory_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : execute_to_memory_ENV_CTRL_string = "XRET";
      default : execute_to_memory_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(memory_to_writeBack_ENV_CTRL)
      EnvCtrlEnum_NONE : memory_to_writeBack_ENV_CTRL_string = "NONE";
      EnvCtrlEnum_XRET : memory_to_writeBack_ENV_CTRL_string = "XRET";
      default : memory_to_writeBack_ENV_CTRL_string = "????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_ALU_CTRL)
      AluCtrlEnum_ADD_SUB : decode_to_execute_ALU_CTRL_string = "ADD_SUB ";
      AluCtrlEnum_SLT_SLTU : decode_to_execute_ALU_CTRL_string = "SLT_SLTU";
      AluCtrlEnum_BITWISE : decode_to_execute_ALU_CTRL_string = "BITWISE ";
      default : decode_to_execute_ALU_CTRL_string = "????????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_ALU_BITWISE_CTRL)
      AluBitwiseCtrlEnum_XOR_1 : decode_to_execute_ALU_BITWISE_CTRL_string = "XOR_1";
      AluBitwiseCtrlEnum_OR_1 : decode_to_execute_ALU_BITWISE_CTRL_string = "OR_1 ";
      AluBitwiseCtrlEnum_AND_1 : decode_to_execute_ALU_BITWISE_CTRL_string = "AND_1";
      default : decode_to_execute_ALU_BITWISE_CTRL_string = "?????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_SHIFT_CTRL)
      ShiftCtrlEnum_DISABLE_1 : decode_to_execute_SHIFT_CTRL_string = "DISABLE_1";
      ShiftCtrlEnum_SLL_1 : decode_to_execute_SHIFT_CTRL_string = "SLL_1    ";
      ShiftCtrlEnum_SRL_1 : decode_to_execute_SHIFT_CTRL_string = "SRL_1    ";
      ShiftCtrlEnum_SRA_1 : decode_to_execute_SHIFT_CTRL_string = "SRA_1    ";
      default : decode_to_execute_SHIFT_CTRL_string = "?????????";
    endcase
  end
  always @(*) begin
    case(decode_to_execute_BRANCH_CTRL)
      BranchCtrlEnum_INC : decode_to_execute_BRANCH_CTRL_string = "INC ";
      BranchCtrlEnum_B : decode_to_execute_BRANCH_CTRL_string = "B   ";
      BranchCtrlEnum_JAL : decode_to_execute_BRANCH_CTRL_string = "JAL ";
      BranchCtrlEnum_JALR : decode_to_execute_BRANCH_CTRL_string = "JALR";
      default : decode_to_execute_BRANCH_CTRL_string = "????";
    endcase
  end
  `endif

  assign memory_MEMORY_READ_DATA = dBus_rsp_data; // @[Stage.scala 30:13]
  assign execute_BRANCH_CALC = {execute_BranchPlugin_branchAdder[31 : 1],1'b0}; // @[Stage.scala 30:13]
  assign execute_BRANCH_DO = _zz_execute_BRANCH_DO_1; // @[Stage.scala 30:13]
  assign writeBack_REGFILE_WRITE_DATA = memory_to_writeBack_REGFILE_WRITE_DATA; // @[Stage.scala 30:13]
  assign execute_REGFILE_WRITE_DATA = _zz_execute_REGFILE_WRITE_DATA; // @[Stage.scala 30:13]
  assign memory_MEMORY_ADDRESS_LOW = execute_to_memory_MEMORY_ADDRESS_LOW; // @[Stage.scala 30:13]
  assign execute_MEMORY_ADDRESS_LOW = dBus_cmd_payload_address[1 : 0]; // @[Stage.scala 30:13]
  assign decode_SRC2 = _zz_decode_SRC2_4; // @[Stage.scala 30:13]
  assign decode_SRC1 = _zz_decode_SRC1; // @[Stage.scala 30:13]
  assign decode_SRC2_FORCE_ZERO = (decode_SRC_ADD_ZERO && (! decode_SRC_USE_SUB_LESS)); // @[Stage.scala 30:13]
  assign decode_RS2 = decode_RegFilePlugin_rs2Data; // @[Stage.scala 30:13]
  assign decode_RS1 = decode_RegFilePlugin_rs1Data; // @[Stage.scala 30:13]
  assign decode_BRANCH_CTRL = _zz_decode_BRANCH_CTRL; // @[Stage.scala 30:13]
  assign _zz_decode_to_execute_BRANCH_CTRL = _zz_decode_to_execute_BRANCH_CTRL_1; // @[Stage.scala 39:14]
  assign decode_SHIFT_CTRL = _zz_decode_SHIFT_CTRL; // @[Stage.scala 30:13]
  assign _zz_decode_to_execute_SHIFT_CTRL = _zz_decode_to_execute_SHIFT_CTRL_1; // @[Stage.scala 39:14]
  assign decode_ALU_BITWISE_CTRL = _zz_decode_ALU_BITWISE_CTRL; // @[Stage.scala 30:13]
  assign _zz_decode_to_execute_ALU_BITWISE_CTRL = _zz_decode_to_execute_ALU_BITWISE_CTRL_1; // @[Stage.scala 39:14]
  assign decode_SRC_LESS_UNSIGNED = _zz_decode_SRC_LESS_UNSIGNED[17]; // @[Stage.scala 30:13]
  assign decode_ALU_CTRL = _zz_decode_ALU_CTRL; // @[Stage.scala 30:13]
  assign _zz_decode_to_execute_ALU_CTRL = _zz_decode_to_execute_ALU_CTRL_1; // @[Stage.scala 39:14]
  assign _zz_memory_to_writeBack_ENV_CTRL = _zz_memory_to_writeBack_ENV_CTRL_1; // @[Stage.scala 39:14]
  assign _zz_execute_to_memory_ENV_CTRL = _zz_execute_to_memory_ENV_CTRL_1; // @[Stage.scala 39:14]
  assign decode_ENV_CTRL = _zz_decode_ENV_CTRL; // @[Stage.scala 30:13]
  assign _zz_decode_to_execute_ENV_CTRL = _zz_decode_to_execute_ENV_CTRL_1; // @[Stage.scala 39:14]
  assign decode_IS_CSR = _zz_decode_SRC_LESS_UNSIGNED[13]; // @[Stage.scala 30:13]
  assign decode_MEMORY_STORE = _zz_decode_SRC_LESS_UNSIGNED[10]; // @[Stage.scala 30:13]
  assign execute_BYPASSABLE_MEMORY_STAGE = decode_to_execute_BYPASSABLE_MEMORY_STAGE; // @[Stage.scala 30:13]
  assign decode_BYPASSABLE_MEMORY_STAGE = _zz_decode_SRC_LESS_UNSIGNED[9]; // @[Stage.scala 30:13]
  assign decode_BYPASSABLE_EXECUTE_STAGE = _zz_decode_SRC_LESS_UNSIGNED[8]; // @[Stage.scala 30:13]
  assign decode_MEMORY_ENABLE = _zz_decode_SRC_LESS_UNSIGNED[3]; // @[Stage.scala 30:13]
  assign decode_CSR_READ_OPCODE = (decode_INSTRUCTION[13 : 7] != 7'h20); // @[Stage.scala 30:13]
  assign decode_CSR_WRITE_OPCODE = (! (((decode_INSTRUCTION[14 : 13] == 2'b01) && (decode_INSTRUCTION[19 : 15] == 5'h0)) || ((decode_INSTRUCTION[14 : 13] == 2'b11) && (decode_INSTRUCTION[19 : 15] == 5'h0)))); // @[Stage.scala 30:13]
  assign writeBack_FORMAL_PC_NEXT = memory_to_writeBack_FORMAL_PC_NEXT; // @[Stage.scala 30:13]
  assign memory_FORMAL_PC_NEXT = execute_to_memory_FORMAL_PC_NEXT; // @[Stage.scala 30:13]
  assign execute_FORMAL_PC_NEXT = decode_to_execute_FORMAL_PC_NEXT; // @[Stage.scala 30:13]
  assign decode_FORMAL_PC_NEXT = (decode_PC + 32'h00000004); // @[Stage.scala 30:13]
  assign memory_PC = execute_to_memory_PC; // @[Stage.scala 30:13]
  assign memory_BRANCH_CALC = execute_to_memory_BRANCH_CALC; // @[Stage.scala 30:13]
  assign memory_BRANCH_DO = execute_to_memory_BRANCH_DO; // @[Stage.scala 30:13]
  assign execute_PC = decode_to_execute_PC; // @[Stage.scala 30:13]
  assign execute_RS1 = decode_to_execute_RS1; // @[Stage.scala 30:13]
  assign execute_BRANCH_CTRL = _zz_execute_BRANCH_CTRL; // @[Stage.scala 30:13]
  assign decode_RS2_USE = _zz_decode_SRC_LESS_UNSIGNED[12]; // @[Stage.scala 30:13]
  assign decode_RS1_USE = _zz_decode_SRC_LESS_UNSIGNED[4]; // @[Stage.scala 30:13]
  assign execute_REGFILE_WRITE_VALID = decode_to_execute_REGFILE_WRITE_VALID; // @[Stage.scala 30:13]
  assign execute_BYPASSABLE_EXECUTE_STAGE = decode_to_execute_BYPASSABLE_EXECUTE_STAGE; // @[Stage.scala 30:13]
  assign memory_REGFILE_WRITE_VALID = execute_to_memory_REGFILE_WRITE_VALID; // @[Stage.scala 30:13]
  assign memory_INSTRUCTION = execute_to_memory_INSTRUCTION; // @[Stage.scala 30:13]
  assign memory_BYPASSABLE_MEMORY_STAGE = execute_to_memory_BYPASSABLE_MEMORY_STAGE; // @[Stage.scala 30:13]
  assign writeBack_REGFILE_WRITE_VALID = memory_to_writeBack_REGFILE_WRITE_VALID; // @[Stage.scala 30:13]
  assign memory_REGFILE_WRITE_DATA = execute_to_memory_REGFILE_WRITE_DATA; // @[Stage.scala 30:13]
  assign execute_SHIFT_CTRL = _zz_execute_SHIFT_CTRL; // @[Stage.scala 30:13]
  assign execute_SRC_LESS_UNSIGNED = decode_to_execute_SRC_LESS_UNSIGNED; // @[Stage.scala 30:13]
  assign execute_SRC2_FORCE_ZERO = decode_to_execute_SRC2_FORCE_ZERO; // @[Stage.scala 30:13]
  assign execute_SRC_USE_SUB_LESS = decode_to_execute_SRC_USE_SUB_LESS; // @[Stage.scala 30:13]
  assign _zz_decode_to_execute_PC = decode_PC; // @[Stage.scala 39:14]
  assign _zz_decode_to_execute_RS2 = decode_RS2; // @[Stage.scala 39:14]
  assign decode_SRC2_CTRL = _zz_decode_SRC2_CTRL; // @[Stage.scala 30:13]
  assign _zz_decode_to_execute_RS1 = decode_RS1; // @[Stage.scala 39:14]
  assign decode_SRC1_CTRL = _zz_decode_SRC1_CTRL; // @[Stage.scala 30:13]
  assign decode_SRC_USE_SUB_LESS = _zz_decode_SRC_LESS_UNSIGNED[2]; // @[Stage.scala 30:13]
  assign decode_SRC_ADD_ZERO = _zz_decode_SRC_LESS_UNSIGNED[20]; // @[Stage.scala 30:13]
  assign execute_SRC_ADD_SUB = execute_SrcPlugin_addSub; // @[Stage.scala 30:13]
  assign execute_SRC_LESS = execute_SrcPlugin_less; // @[Stage.scala 30:13]
  assign execute_ALU_CTRL = _zz_execute_ALU_CTRL; // @[Stage.scala 30:13]
  assign execute_SRC2 = decode_to_execute_SRC2; // @[Stage.scala 30:13]
  assign execute_ALU_BITWISE_CTRL = _zz_execute_ALU_BITWISE_CTRL; // @[Stage.scala 30:13]
  assign _zz_lastStageRegFileWrite_payload_address = writeBack_INSTRUCTION; // @[Stage.scala 39:14]
  assign _zz_lastStageRegFileWrite_valid = writeBack_REGFILE_WRITE_VALID; // @[Stage.scala 39:14]
  always @(*) begin
    _zz_1 = 1'b0; // @[when.scala 47:16]
    if(lastStageRegFileWrite_valid) begin
      _zz_1 = 1'b1; // @[when.scala 52:10]
    end
  end

  assign decode_INSTRUCTION_ANTICIPATED = (decode_arbitration_isStuck ? decode_INSTRUCTION : IBusSimplePlugin_iBusRsp_output_payload_rsp_inst); // @[Stage.scala 30:13]
  always @(*) begin
    decode_REGFILE_WRITE_VALID = _zz_decode_SRC_LESS_UNSIGNED[7]; // @[Stage.scala 30:13]
    if(when_RegFilePlugin_l63) begin
      decode_REGFILE_WRITE_VALID = 1'b0; // @[RegFilePlugin.scala 64:41]
    end
  end

  always @(*) begin
    _zz_execute_to_memory_REGFILE_WRITE_DATA = execute_REGFILE_WRITE_DATA; // @[Stage.scala 39:14]
    if(when_CsrPlugin_l1509) begin
      _zz_execute_to_memory_REGFILE_WRITE_DATA = CsrPlugin_csrMapping_readDataSignal; // @[CsrPlugin.scala 1510:59]
    end
    if(when_ShiftPlugins_l169) begin
      _zz_execute_to_memory_REGFILE_WRITE_DATA = _zz_execute_to_memory_REGFILE_WRITE_DATA_1; // @[ShiftPlugins.scala 170:36]
    end
  end

  assign execute_SRC1 = decode_to_execute_SRC1; // @[Stage.scala 30:13]
  assign execute_CSR_READ_OPCODE = decode_to_execute_CSR_READ_OPCODE; // @[Stage.scala 30:13]
  assign execute_CSR_WRITE_OPCODE = decode_to_execute_CSR_WRITE_OPCODE; // @[Stage.scala 30:13]
  assign execute_IS_CSR = decode_to_execute_IS_CSR; // @[Stage.scala 30:13]
  assign memory_ENV_CTRL = _zz_memory_ENV_CTRL; // @[Stage.scala 30:13]
  assign execute_ENV_CTRL = _zz_execute_ENV_CTRL; // @[Stage.scala 30:13]
  assign writeBack_ENV_CTRL = _zz_writeBack_ENV_CTRL; // @[Stage.scala 30:13]
  always @(*) begin
    _zz_lastStageRegFileWrite_payload_data = writeBack_REGFILE_WRITE_DATA; // @[Stage.scala 39:14]
    if(when_DBusSimplePlugin_l558) begin
      _zz_lastStageRegFileWrite_payload_data = writeBack_DBusSimplePlugin_rspFormated; // @[DBusSimplePlugin.scala 559:36]
    end
  end

  assign writeBack_MEMORY_ENABLE = memory_to_writeBack_MEMORY_ENABLE; // @[Stage.scala 30:13]
  assign writeBack_MEMORY_ADDRESS_LOW = memory_to_writeBack_MEMORY_ADDRESS_LOW; // @[Stage.scala 30:13]
  assign writeBack_MEMORY_READ_DATA = memory_to_writeBack_MEMORY_READ_DATA; // @[Stage.scala 30:13]
  assign memory_MEMORY_STORE = execute_to_memory_MEMORY_STORE; // @[Stage.scala 30:13]
  assign memory_MEMORY_ENABLE = execute_to_memory_MEMORY_ENABLE; // @[Stage.scala 30:13]
  assign execute_SRC_ADD = execute_SrcPlugin_addSub; // @[Stage.scala 30:13]
  assign execute_RS2 = decode_to_execute_RS2; // @[Stage.scala 30:13]
  assign execute_INSTRUCTION = decode_to_execute_INSTRUCTION; // @[Stage.scala 30:13]
  assign execute_MEMORY_STORE = decode_to_execute_MEMORY_STORE; // @[Stage.scala 30:13]
  assign execute_MEMORY_ENABLE = decode_to_execute_MEMORY_ENABLE; // @[Stage.scala 30:13]
  assign execute_ALIGNEMENT_FAULT = 1'b0; // @[Stage.scala 30:13]
  always @(*) begin
    _zz_memory_to_writeBack_FORMAL_PC_NEXT = memory_FORMAL_PC_NEXT; // @[Stage.scala 39:14]
    if(BranchPlugin_jumpInterface_valid) begin
      _zz_memory_to_writeBack_FORMAL_PC_NEXT = BranchPlugin_jumpInterface_payload; // @[Fetcher.scala 437:47]
    end
  end

  assign decode_PC = IBusSimplePlugin_injector_decodeInput_payload_pc; // @[Stage.scala 30:13]
  assign decode_INSTRUCTION = IBusSimplePlugin_injector_decodeInput_payload_rsp_inst; // @[Stage.scala 30:13]
  assign writeBack_PC = memory_to_writeBack_PC; // @[Stage.scala 30:13]
  assign writeBack_INSTRUCTION = memory_to_writeBack_INSTRUCTION; // @[Stage.scala 30:13]
  assign decode_arbitration_haltItself = 1'b0; // @[Stage.scala 49:23]
  always @(*) begin
    decode_arbitration_haltByOther = 1'b0; // @[Stage.scala 50:23]
    if(CsrPlugin_pipelineLiberator_active) begin
      decode_arbitration_haltByOther = 1'b1; // @[CsrPlugin.scala 1255:42]
    end
    if(when_CsrPlugin_l1449) begin
      decode_arbitration_haltByOther = 1'b1; // @[CsrPlugin.scala 1449:38]
    end
    if(when_HazardSimplePlugin_l113) begin
      decode_arbitration_haltByOther = 1'b1; // @[HazardSimplePlugin.scala 114:43]
    end
  end

  always @(*) begin
    decode_arbitration_removeIt = 1'b0; // @[Stage.scala 51:23]
    if(decode_arbitration_isFlushed) begin
      decode_arbitration_removeIt = 1'b1; // @[Pipeline.scala 134:36]
    end
  end

  assign decode_arbitration_flushIt = 1'b0; // @[Stage.scala 52:22]
  assign decode_arbitration_flushNext = 1'b0; // @[Stage.scala 53:24]
  always @(*) begin
    execute_arbitration_haltItself = 1'b0; // @[Stage.scala 49:23]
    if(when_DBusSimplePlugin_l428) begin
      execute_arbitration_haltItself = 1'b1; // @[DBusSimplePlugin.scala 429:32]
    end
    if(when_CsrPlugin_l1513) begin
      if(execute_CsrPlugin_blockedBySideEffects) begin
        execute_arbitration_haltItself = 1'b1; // @[CsrPlugin.scala 1514:34]
      end
    end
    if(when_ShiftPlugins_l169) begin
      if(when_ShiftPlugins_l184) begin
        execute_arbitration_haltItself = 1'b1; // @[ShiftPlugins.scala 185:34]
      end
    end
  end

  assign execute_arbitration_haltByOther = 1'b0; // @[Stage.scala 50:23]
  always @(*) begin
    execute_arbitration_removeIt = 1'b0; // @[Stage.scala 51:23]
    if(execute_arbitration_isFlushed) begin
      execute_arbitration_removeIt = 1'b1; // @[Pipeline.scala 134:36]
    end
  end

  assign execute_arbitration_flushIt = 1'b0; // @[Stage.scala 52:22]
  assign execute_arbitration_flushNext = 1'b0; // @[Stage.scala 53:24]
  always @(*) begin
    memory_arbitration_haltItself = 1'b0; // @[Stage.scala 49:23]
    if(when_DBusSimplePlugin_l482) begin
      memory_arbitration_haltItself = 1'b1; // @[DBusSimplePlugin.scala 482:30]
    end
  end

  assign memory_arbitration_haltByOther = 1'b0; // @[Stage.scala 50:23]
  always @(*) begin
    memory_arbitration_removeIt = 1'b0; // @[Stage.scala 51:23]
    if(memory_arbitration_isFlushed) begin
      memory_arbitration_removeIt = 1'b1; // @[Pipeline.scala 134:36]
    end
  end

  assign memory_arbitration_flushIt = 1'b0; // @[Stage.scala 52:22]
  always @(*) begin
    memory_arbitration_flushNext = 1'b0; // @[Stage.scala 53:24]
    if(BranchPlugin_jumpInterface_valid) begin
      memory_arbitration_flushNext = 1'b1; // @[BranchPlugin.scala 215:29]
    end
  end

  assign writeBack_arbitration_haltItself = 1'b0; // @[Stage.scala 49:23]
  assign writeBack_arbitration_haltByOther = 1'b0; // @[Stage.scala 50:23]
  always @(*) begin
    writeBack_arbitration_removeIt = 1'b0; // @[Stage.scala 51:23]
    if(writeBack_arbitration_isFlushed) begin
      writeBack_arbitration_removeIt = 1'b1; // @[Pipeline.scala 134:36]
    end
  end

  assign writeBack_arbitration_flushIt = 1'b0; // @[Stage.scala 52:22]
  always @(*) begin
    writeBack_arbitration_flushNext = 1'b0; // @[Stage.scala 53:24]
    if(when_CsrPlugin_l1312) begin
      writeBack_arbitration_flushNext = 1'b1; // @[CsrPlugin.scala 1318:41]
    end
    if(when_CsrPlugin_l1378) begin
      writeBack_arbitration_flushNext = 1'b1; // @[CsrPlugin.scala 1381:43]
    end
  end

  assign lastStageInstruction = writeBack_INSTRUCTION; // @[Misc.scala 552:9]
  assign lastStagePc = writeBack_PC; // @[Misc.scala 552:9]
  assign lastStageIsValid = writeBack_arbitration_isValid; // @[Misc.scala 552:9]
  assign lastStageIsFiring = writeBack_arbitration_isFiring; // @[Misc.scala 552:9]
  always @(*) begin
    IBusSimplePlugin_fetcherHalt = 1'b0; // @[Fetcher.scala 67:19]
    if(when_CsrPlugin_l1312) begin
      IBusSimplePlugin_fetcherHalt = 1'b1; // @[Fetcher.scala 53:45]
    end
    if(when_CsrPlugin_l1378) begin
      IBusSimplePlugin_fetcherHalt = 1'b1; // @[Fetcher.scala 53:45]
    end
  end

  assign IBusSimplePlugin_forceNoDecodeCond = 1'b0; // @[Fetcher.scala 68:25]
  always @(*) begin
    IBusSimplePlugin_incomingInstruction = 1'b0; // @[Fetcher.scala 69:27]
    if(IBusSimplePlugin_iBusRsp_stages_1_input_valid) begin
      IBusSimplePlugin_incomingInstruction = 1'b1; // @[Fetcher.scala 243:27]
    end
    if(IBusSimplePlugin_injector_decodeInput_valid) begin
      IBusSimplePlugin_incomingInstruction = 1'b1; // @[Fetcher.scala 317:29]
    end
  end

  assign CsrPlugin_csrMapping_allowCsrSignal = 1'b0; // @[CsrPlugin.scala 359:24]
  assign CsrPlugin_csrMapping_readDataSignal = CsrPlugin_csrMapping_readDataInit; // @[CsrPlugin.scala 362:18]
  assign CsrPlugin_inWfi = 1'b0; // @[CsrPlugin.scala 553:13]
  assign CsrPlugin_thirdPartyWake = 1'b0; // @[CsrPlugin.scala 555:22]
  always @(*) begin
    CsrPlugin_jumpInterface_valid = 1'b0; // @[CsrPlugin.scala 597:25]
    if(when_CsrPlugin_l1312) begin
      CsrPlugin_jumpInterface_valid = 1'b1; // @[CsrPlugin.scala 1316:37]
    end
    if(when_CsrPlugin_l1378) begin
      CsrPlugin_jumpInterface_valid = 1'b1; // @[CsrPlugin.scala 1380:31]
    end
  end

  always @(*) begin
    CsrPlugin_jumpInterface_payload = 32'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx; // @[UInt.scala 467:20]
    if(when_CsrPlugin_l1312) begin
      CsrPlugin_jumpInterface_payload = {CsrPlugin_xtvec_base,2'b00}; // @[CsrPlugin.scala 1317:37]
    end
    if(when_CsrPlugin_l1378) begin
      case(switch_CsrPlugin_l1382)
        2'b11 : begin
          CsrPlugin_jumpInterface_payload = CsrPlugin_mepc; // @[CsrPlugin.scala 1387:37]
        end
        default : begin
        end
      endcase
    end
  end

  assign CsrPlugin_forceMachineWire = 1'b0; // @[CsrPlugin.scala 617:24]
  assign CsrPlugin_allowInterrupts = 1'b1; // @[CsrPlugin.scala 622:23]
  assign CsrPlugin_allowException = 1'b1; // @[CsrPlugin.scala 623:22]
  assign CsrPlugin_allowEbreakException = 1'b1; // @[CsrPlugin.scala 624:28]
  assign CsrPlugin_xretAwayFromMachine = 1'b0; // @[CsrPlugin.scala 639:27]
  assign BranchPlugin_inDebugNoFetchFlag = 1'b0; // @[BranchPlugin.scala 155:26]
  assign IBusSimplePlugin_externalFlush = ({writeBack_arbitration_flushNext,{memory_arbitration_flushNext,{execute_arbitration_flushNext,decode_arbitration_flushNext}}} != 4'b0000); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_jump_pcLoad_valid = ({BranchPlugin_jumpInterface_valid,CsrPlugin_jumpInterface_valid} != 2'b00); // @[Fetcher.scala 116:20]
  assign _zz_IBusSimplePlugin_jump_pcLoad_payload = {BranchPlugin_jumpInterface_valid,CsrPlugin_jumpInterface_valid}; // @[BaseType.scala 318:22]
  assign IBusSimplePlugin_jump_pcLoad_payload = (_zz_IBusSimplePlugin_jump_pcLoad_payload_1[0] ? CsrPlugin_jumpInterface_payload : BranchPlugin_jumpInterface_payload); // @[Fetcher.scala 117:22]
  always @(*) begin
    IBusSimplePlugin_fetchPc_correction = 1'b0; // @[Fetcher.scala 129:24]
    if(IBusSimplePlugin_jump_pcLoad_valid) begin
      IBusSimplePlugin_fetchPc_correction = 1'b1; // @[Fetcher.scala 156:20]
    end
  end

  assign IBusSimplePlugin_fetchPc_output_fire = (IBusSimplePlugin_fetchPc_output_valid && IBusSimplePlugin_fetchPc_output_ready); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_fetchPc_corrected = (IBusSimplePlugin_fetchPc_correction || IBusSimplePlugin_fetchPc_correctionReg); // @[BaseType.scala 305:24]
  always @(*) begin
    IBusSimplePlugin_fetchPc_pcRegPropagate = 1'b0; // @[Fetcher.scala 132:28]
    if(IBusSimplePlugin_iBusRsp_stages_1_input_ready) begin
      IBusSimplePlugin_fetchPc_pcRegPropagate = 1'b1; // @[Fetcher.scala 235:34]
    end
  end

  assign when_Fetcher_l134 = (IBusSimplePlugin_fetchPc_correction || IBusSimplePlugin_fetchPc_pcRegPropagate); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_fetchPc_output_fire_1 = (IBusSimplePlugin_fetchPc_output_valid && IBusSimplePlugin_fetchPc_output_ready); // @[BaseType.scala 305:24]
  assign when_Fetcher_l134_1 = ((! IBusSimplePlugin_fetchPc_output_valid) && IBusSimplePlugin_fetchPc_output_ready); // @[BaseType.scala 305:24]
  always @(*) begin
    IBusSimplePlugin_fetchPc_pc = (IBusSimplePlugin_fetchPc_pcReg + _zz_IBusSimplePlugin_fetchPc_pc); // @[BaseType.scala 299:24]
    if(IBusSimplePlugin_jump_pcLoad_valid) begin
      IBusSimplePlugin_fetchPc_pc = IBusSimplePlugin_jump_pcLoad_payload; // @[Fetcher.scala 157:12]
    end
    IBusSimplePlugin_fetchPc_pc[0] = 1'b0; // @[Fetcher.scala 165:13]
    IBusSimplePlugin_fetchPc_pc[1] = 1'b0; // @[Fetcher.scala 166:32]
  end

  always @(*) begin
    IBusSimplePlugin_fetchPc_flushed = 1'b0; // @[Fetcher.scala 138:21]
    if(IBusSimplePlugin_jump_pcLoad_valid) begin
      IBusSimplePlugin_fetchPc_flushed = 1'b1; // @[Fetcher.scala 158:17]
    end
  end

  assign when_Fetcher_l161 = (IBusSimplePlugin_fetchPc_booted && ((IBusSimplePlugin_fetchPc_output_ready || IBusSimplePlugin_fetchPc_correction) || IBusSimplePlugin_fetchPc_pcRegPropagate)); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_fetchPc_output_valid = ((! IBusSimplePlugin_fetcherHalt) && IBusSimplePlugin_fetchPc_booted); // @[Fetcher.scala 168:20]
  assign IBusSimplePlugin_fetchPc_output_payload = IBusSimplePlugin_fetchPc_pc; // @[Fetcher.scala 169:22]
  assign IBusSimplePlugin_iBusRsp_redoFetch = 1'b0; // @[Fetcher.scala 210:23]
  assign IBusSimplePlugin_iBusRsp_stages_0_input_valid = IBusSimplePlugin_fetchPc_output_valid; // @[Stream.scala 294:16]
  assign IBusSimplePlugin_fetchPc_output_ready = IBusSimplePlugin_iBusRsp_stages_0_input_ready; // @[Stream.scala 295:16]
  assign IBusSimplePlugin_iBusRsp_stages_0_input_payload = IBusSimplePlugin_fetchPc_output_payload; // @[Stream.scala 296:18]
  always @(*) begin
    IBusSimplePlugin_iBusRsp_stages_0_halt = 1'b0; // @[Fetcher.scala 219:16]
    if(when_IBusSimplePlugin_l305) begin
      IBusSimplePlugin_iBusRsp_stages_0_halt = 1'b1; // @[IBusSimplePlugin.scala 305:20]
    end
  end

  assign _zz_IBusSimplePlugin_iBusRsp_stages_0_input_ready = (! IBusSimplePlugin_iBusRsp_stages_0_halt); // @[BaseType.scala 299:24]
  assign IBusSimplePlugin_iBusRsp_stages_0_input_ready = (IBusSimplePlugin_iBusRsp_stages_0_output_ready && _zz_IBusSimplePlugin_iBusRsp_stages_0_input_ready); // @[Stream.scala 427:16]
  assign IBusSimplePlugin_iBusRsp_stages_0_output_valid = (IBusSimplePlugin_iBusRsp_stages_0_input_valid && _zz_IBusSimplePlugin_iBusRsp_stages_0_input_ready); // @[Stream.scala 294:16]
  assign IBusSimplePlugin_iBusRsp_stages_0_output_payload = IBusSimplePlugin_iBusRsp_stages_0_input_payload; // @[Stream.scala 296:18]
  assign IBusSimplePlugin_iBusRsp_stages_1_halt = 1'b0; // @[Fetcher.scala 219:16]
  assign _zz_IBusSimplePlugin_iBusRsp_stages_1_input_ready = (! IBusSimplePlugin_iBusRsp_stages_1_halt); // @[BaseType.scala 299:24]
  assign IBusSimplePlugin_iBusRsp_stages_1_input_ready = (IBusSimplePlugin_iBusRsp_stages_1_output_ready && _zz_IBusSimplePlugin_iBusRsp_stages_1_input_ready); // @[Stream.scala 427:16]
  assign IBusSimplePlugin_iBusRsp_stages_1_output_valid = (IBusSimplePlugin_iBusRsp_stages_1_input_valid && _zz_IBusSimplePlugin_iBusRsp_stages_1_input_ready); // @[Stream.scala 294:16]
  assign IBusSimplePlugin_iBusRsp_stages_1_output_payload = IBusSimplePlugin_iBusRsp_stages_1_input_payload; // @[Stream.scala 296:18]
  assign IBusSimplePlugin_iBusRsp_flush = (IBusSimplePlugin_externalFlush || IBusSimplePlugin_iBusRsp_redoFetch); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_iBusRsp_stages_0_output_ready = _zz_IBusSimplePlugin_iBusRsp_stages_0_output_ready; // @[Stream.scala 304:16]
  assign _zz_IBusSimplePlugin_iBusRsp_stages_0_output_ready = ((1'b0 && (! _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid)) || IBusSimplePlugin_iBusRsp_stages_1_input_ready); // @[Misc.scala 148:20]
  assign _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid = _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid_1; // @[Misc.scala 158:17]
  assign IBusSimplePlugin_iBusRsp_stages_1_input_valid = _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid; // @[Stream.scala 303:16]
  assign IBusSimplePlugin_iBusRsp_stages_1_input_payload = IBusSimplePlugin_fetchPc_pcReg; // @[Fetcher.scala 234:31]
  always @(*) begin
    IBusSimplePlugin_iBusRsp_readyForError = 1'b1; // @[Fetcher.scala 241:27]
    if(IBusSimplePlugin_injector_decodeInput_valid) begin
      IBusSimplePlugin_iBusRsp_readyForError = 1'b0; // @[Fetcher.scala 316:40]
    end
    if(when_Fetcher_l323) begin
      IBusSimplePlugin_iBusRsp_readyForError = 1'b0; // @[Fetcher.scala 323:55]
    end
  end

  assign IBusSimplePlugin_iBusRsp_output_ready = ((1'b0 && (! IBusSimplePlugin_injector_decodeInput_valid)) || IBusSimplePlugin_injector_decodeInput_ready); // @[Misc.scala 148:20]
  assign IBusSimplePlugin_injector_decodeInput_valid = _zz_IBusSimplePlugin_injector_decodeInput_valid; // @[Misc.scala 158:17]
  assign IBusSimplePlugin_injector_decodeInput_payload_pc = _zz_IBusSimplePlugin_injector_decodeInput_payload_pc; // @[Misc.scala 159:19]
  assign IBusSimplePlugin_injector_decodeInput_payload_rsp_error = _zz_IBusSimplePlugin_injector_decodeInput_payload_rsp_error; // @[Misc.scala 159:19]
  assign IBusSimplePlugin_injector_decodeInput_payload_rsp_inst = _zz_IBusSimplePlugin_injector_decodeInput_payload_rsp_inst; // @[Misc.scala 159:19]
  assign IBusSimplePlugin_injector_decodeInput_payload_isRvc = _zz_IBusSimplePlugin_injector_decodeInput_payload_isRvc; // @[Misc.scala 159:19]
  assign when_Fetcher_l323 = (! IBusSimplePlugin_pcValids_0); // @[BaseType.scala 299:24]
  assign when_Fetcher_l332 = (! (! IBusSimplePlugin_iBusRsp_stages_1_input_ready)); // @[BaseType.scala 299:24]
  assign when_Fetcher_l332_1 = (! (! IBusSimplePlugin_injector_decodeInput_ready)); // @[BaseType.scala 299:24]
  assign when_Fetcher_l332_2 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Fetcher_l332_3 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Fetcher_l332_4 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign IBusSimplePlugin_pcValids_0 = IBusSimplePlugin_injector_nextPcCalc_valids_1; // @[Fetcher.scala 348:18]
  assign IBusSimplePlugin_pcValids_1 = IBusSimplePlugin_injector_nextPcCalc_valids_2; // @[Fetcher.scala 348:18]
  assign IBusSimplePlugin_pcValids_2 = IBusSimplePlugin_injector_nextPcCalc_valids_3; // @[Fetcher.scala 348:18]
  assign IBusSimplePlugin_pcValids_3 = IBusSimplePlugin_injector_nextPcCalc_valids_4; // @[Fetcher.scala 348:18]
  assign IBusSimplePlugin_injector_decodeInput_ready = (! decode_arbitration_isStuck); // @[Fetcher.scala 351:25]
  always @(*) begin
    decode_arbitration_isValid = IBusSimplePlugin_injector_decodeInput_valid; // @[Fetcher.scala 352:34]
    if(IBusSimplePlugin_forceNoDecodeCond) begin
      decode_arbitration_isValid = 1'b0; // @[Fetcher.scala 415:36]
    end
  end

  assign IBusSimplePlugin_cmd_ready = (! IBusSimplePlugin_cmd_rValid); // @[Stream.scala 380:16]
  assign IBusSimplePlugin_cmd_s2mPipe_valid = (IBusSimplePlugin_cmd_valid || IBusSimplePlugin_cmd_rValid); // @[Stream.scala 382:19]
  assign IBusSimplePlugin_cmd_s2mPipe_payload_pc = (IBusSimplePlugin_cmd_rValid ? IBusSimplePlugin_cmd_rData_pc : IBusSimplePlugin_cmd_payload_pc); // @[Stream.scala 383:21]
  assign iBus_cmd_valid = IBusSimplePlugin_cmd_s2mPipe_valid; // @[Stream.scala 294:16]
  assign IBusSimplePlugin_cmd_s2mPipe_ready = iBus_cmd_ready; // @[Stream.scala 295:16]
  assign iBus_cmd_payload_pc = IBusSimplePlugin_cmd_s2mPipe_payload_pc; // @[Stream.scala 296:18]
  assign IBusSimplePlugin_pending_next = (_zz_IBusSimplePlugin_pending_next - IBusSimplePlugin_pending_dec); // @[BaseType.scala 299:24]
  assign IBusSimplePlugin_cmdFork_canEmit = (IBusSimplePlugin_iBusRsp_stages_0_output_ready && (IBusSimplePlugin_pending_value != 1'b1)); // @[BaseType.scala 305:24]
  assign when_IBusSimplePlugin_l305 = (IBusSimplePlugin_iBusRsp_stages_0_input_valid && ((! IBusSimplePlugin_cmdFork_canEmit) || (! IBusSimplePlugin_cmd_ready))); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_cmd_valid = (IBusSimplePlugin_iBusRsp_stages_0_input_valid && IBusSimplePlugin_cmdFork_canEmit); // @[IBusSimplePlugin.scala 306:19]
  assign IBusSimplePlugin_cmd_fire = (IBusSimplePlugin_cmd_valid && IBusSimplePlugin_cmd_ready); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_pending_inc = IBusSimplePlugin_cmd_fire; // @[IBusSimplePlugin.scala 307:21]
  assign IBusSimplePlugin_cmd_payload_pc = {IBusSimplePlugin_iBusRsp_stages_0_input_payload[31 : 2],2'b00}; // @[IBusSimplePlugin.scala 347:16]
  assign iBus_rsp_toStream_valid = iBus_rsp_valid; // @[Flow.scala 72:15]
  assign iBus_rsp_toStream_payload_error = iBus_rsp_payload_error; // @[Flow.scala 73:17]
  assign iBus_rsp_toStream_payload_inst = iBus_rsp_payload_inst; // @[Flow.scala 73:17]
  assign iBus_rsp_toStream_ready = IBusSimplePlugin_rspJoin_rspBuffer_c_io_push_ready; // @[Stream.scala 295:16]
  assign IBusSimplePlugin_rspJoin_rspBuffer_flush = ((IBusSimplePlugin_rspJoin_rspBuffer_discardCounter != 1'b0) || IBusSimplePlugin_iBusRsp_flush); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_valid = (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid && (IBusSimplePlugin_rspJoin_rspBuffer_discardCounter == 1'b0)); // @[IBusSimplePlugin.scala 366:24]
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_payload_error = IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_error; // @[IBusSimplePlugin.scala 367:26]
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_payload_inst = IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_payload_inst; // @[IBusSimplePlugin.scala 367:26]
  assign IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_ready = (IBusSimplePlugin_rspJoin_rspBuffer_output_ready || IBusSimplePlugin_rspJoin_rspBuffer_flush); // @[IBusSimplePlugin.scala 368:26]
  assign cpu_1_IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_fire = (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid && IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_ready); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_pending_dec = cpu_1_IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_fire; // @[IBusSimplePlugin.scala 370:23]
  assign IBusSimplePlugin_rspJoin_fetchRsp_pc = IBusSimplePlugin_iBusRsp_stages_1_output_payload; // @[IBusSimplePlugin.scala 374:21]
  always @(*) begin
    IBusSimplePlugin_rspJoin_fetchRsp_rsp_error = IBusSimplePlugin_rspJoin_rspBuffer_output_payload_error; // @[IBusSimplePlugin.scala 375:22]
    if(when_IBusSimplePlugin_l376) begin
      IBusSimplePlugin_rspJoin_fetchRsp_rsp_error = 1'b0; // @[IBusSimplePlugin.scala 376:37]
    end
  end

  assign IBusSimplePlugin_rspJoin_fetchRsp_rsp_inst = IBusSimplePlugin_rspJoin_rspBuffer_output_payload_inst; // @[IBusSimplePlugin.scala 375:22]
  assign when_IBusSimplePlugin_l376 = (! IBusSimplePlugin_rspJoin_rspBuffer_output_valid); // @[BaseType.scala 299:24]
  assign IBusSimplePlugin_rspJoin_exceptionDetected = 1'b0; // @[IBusSimplePlugin.scala 384:33]
  assign IBusSimplePlugin_rspJoin_join_valid = (IBusSimplePlugin_iBusRsp_stages_1_output_valid && IBusSimplePlugin_rspJoin_rspBuffer_output_valid); // @[IBusSimplePlugin.scala 385:20]
  assign IBusSimplePlugin_rspJoin_join_payload_pc = IBusSimplePlugin_rspJoin_fetchRsp_pc; // @[IBusSimplePlugin.scala 386:22]
  assign IBusSimplePlugin_rspJoin_join_payload_rsp_error = IBusSimplePlugin_rspJoin_fetchRsp_rsp_error; // @[IBusSimplePlugin.scala 386:22]
  assign IBusSimplePlugin_rspJoin_join_payload_rsp_inst = IBusSimplePlugin_rspJoin_fetchRsp_rsp_inst; // @[IBusSimplePlugin.scala 386:22]
  assign IBusSimplePlugin_rspJoin_join_payload_isRvc = IBusSimplePlugin_rspJoin_fetchRsp_isRvc; // @[IBusSimplePlugin.scala 386:22]
  assign IBusSimplePlugin_rspJoin_join_fire = (IBusSimplePlugin_rspJoin_join_valid && IBusSimplePlugin_rspJoin_join_ready); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_iBusRsp_stages_1_output_ready = (IBusSimplePlugin_iBusRsp_stages_1_output_valid ? IBusSimplePlugin_rspJoin_join_fire : IBusSimplePlugin_rspJoin_join_ready); // @[IBusSimplePlugin.scala 387:34]
  assign IBusSimplePlugin_rspJoin_join_fire_1 = (IBusSimplePlugin_rspJoin_join_valid && IBusSimplePlugin_rspJoin_join_ready); // @[BaseType.scala 305:24]
  assign IBusSimplePlugin_rspJoin_rspBuffer_output_ready = IBusSimplePlugin_rspJoin_join_fire_1; // @[IBusSimplePlugin.scala 388:32]
  assign _zz_IBusSimplePlugin_iBusRsp_output_valid = (! IBusSimplePlugin_rspJoin_exceptionDetected); // @[BaseType.scala 299:24]
  assign IBusSimplePlugin_rspJoin_join_ready = (IBusSimplePlugin_iBusRsp_output_ready && _zz_IBusSimplePlugin_iBusRsp_output_valid); // @[Stream.scala 427:16]
  assign IBusSimplePlugin_iBusRsp_output_valid = (IBusSimplePlugin_rspJoin_join_valid && _zz_IBusSimplePlugin_iBusRsp_output_valid); // @[Stream.scala 294:16]
  assign IBusSimplePlugin_iBusRsp_output_payload_pc = IBusSimplePlugin_rspJoin_join_payload_pc; // @[Stream.scala 296:18]
  assign IBusSimplePlugin_iBusRsp_output_payload_rsp_error = IBusSimplePlugin_rspJoin_join_payload_rsp_error; // @[Stream.scala 296:18]
  assign IBusSimplePlugin_iBusRsp_output_payload_rsp_inst = IBusSimplePlugin_rspJoin_join_payload_rsp_inst; // @[Stream.scala 296:18]
  assign IBusSimplePlugin_iBusRsp_output_payload_isRvc = IBusSimplePlugin_rspJoin_join_payload_isRvc; // @[Stream.scala 296:18]
  assign _zz_dBus_cmd_valid = 1'b0; // @[DBusSimplePlugin.scala 404:127]
  always @(*) begin
    execute_DBusSimplePlugin_skipCmd = 1'b0; // @[DBusSimplePlugin.scala 417:21]
    if(execute_ALIGNEMENT_FAULT) begin
      execute_DBusSimplePlugin_skipCmd = 1'b1; // @[DBusSimplePlugin.scala 418:15]
    end
  end

  assign dBus_cmd_valid = (((((execute_arbitration_isValid && execute_MEMORY_ENABLE) && (! execute_arbitration_isStuckByOthers)) && (! execute_arbitration_isFlushed)) && (! execute_DBusSimplePlugin_skipCmd)) && (! _zz_dBus_cmd_valid)); // @[DBusSimplePlugin.scala 420:22]
  assign dBus_cmd_payload_wr = execute_MEMORY_STORE; // @[DBusSimplePlugin.scala 421:19]
  assign dBus_cmd_payload_size = execute_INSTRUCTION[13 : 12]; // @[DBusSimplePlugin.scala 422:21]
  always @(*) begin
    case(dBus_cmd_payload_size)
      2'b00 : begin
        _zz_dBus_cmd_payload_data = {{{execute_RS2[7 : 0],execute_RS2[7 : 0]},execute_RS2[7 : 0]},execute_RS2[7 : 0]}; // @[Misc.scala 239:22]
      end
      2'b01 : begin
        _zz_dBus_cmd_payload_data = {execute_RS2[15 : 0],execute_RS2[15 : 0]}; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_dBus_cmd_payload_data = execute_RS2[31 : 0]; // @[Misc.scala 235:22]
      end
    endcase
  end

  assign dBus_cmd_payload_data = _zz_dBus_cmd_payload_data; // @[DBusSimplePlugin.scala 423:29]
  assign when_DBusSimplePlugin_l428 = ((((execute_arbitration_isValid && execute_MEMORY_ENABLE) && (! dBus_cmd_ready)) && (! execute_DBusSimplePlugin_skipCmd)) && (! _zz_dBus_cmd_valid)); // @[BaseType.scala 305:24]
  always @(*) begin
    case(dBus_cmd_payload_size)
      2'b00 : begin
        _zz_execute_DBusSimplePlugin_formalMask = 4'b0001; // @[Misc.scala 239:22]
      end
      2'b01 : begin
        _zz_execute_DBusSimplePlugin_formalMask = 4'b0011; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_execute_DBusSimplePlugin_formalMask = 4'b1111; // @[Misc.scala 235:22]
      end
    endcase
  end

  assign execute_DBusSimplePlugin_formalMask = (_zz_execute_DBusSimplePlugin_formalMask <<< dBus_cmd_payload_address[1 : 0]); // @[BaseType.scala 299:24]
  assign dBus_cmd_payload_address = execute_SRC_ADD; // @[DBusSimplePlugin.scala 458:26]
  assign when_DBusSimplePlugin_l482 = (((memory_arbitration_isValid && memory_MEMORY_ENABLE) && (! memory_MEMORY_STORE)) && ((! dBus_rsp_ready) || 1'b0)); // @[BaseType.scala 305:24]
  always @(*) begin
    writeBack_DBusSimplePlugin_rspShifted = writeBack_MEMORY_READ_DATA; // @[DBusSimplePlugin.scala 530:18]
    case(writeBack_MEMORY_ADDRESS_LOW)
      2'b01 : begin
        writeBack_DBusSimplePlugin_rspShifted[7 : 0] = writeBack_MEMORY_READ_DATA[15 : 8]; // @[DBusSimplePlugin.scala 539:40]
      end
      2'b10 : begin
        writeBack_DBusSimplePlugin_rspShifted[15 : 0] = writeBack_MEMORY_READ_DATA[31 : 16]; // @[DBusSimplePlugin.scala 540:41]
      end
      2'b11 : begin
        writeBack_DBusSimplePlugin_rspShifted[7 : 0] = writeBack_MEMORY_READ_DATA[31 : 24]; // @[DBusSimplePlugin.scala 541:40]
      end
      default : begin
      end
    endcase
  end

  assign switch_Misc_l226 = writeBack_INSTRUCTION[13 : 12]; // @[BaseType.scala 299:24]
  assign _zz_writeBack_DBusSimplePlugin_rspFormated = (writeBack_DBusSimplePlugin_rspShifted[7] && (! writeBack_INSTRUCTION[14])); // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[31] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[30] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[29] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[28] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[27] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[26] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[25] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[24] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[23] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[22] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[21] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[20] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[19] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[18] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[17] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[16] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[15] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[14] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[13] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[12] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[11] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[10] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[9] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[8] = _zz_writeBack_DBusSimplePlugin_rspFormated; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_1[7 : 0] = writeBack_DBusSimplePlugin_rspShifted[7 : 0]; // @[Literal.scala 99:91]
  end

  assign _zz_writeBack_DBusSimplePlugin_rspFormated_2 = (writeBack_DBusSimplePlugin_rspShifted[15] && (! writeBack_INSTRUCTION[14])); // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[31] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[30] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[29] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[28] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[27] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[26] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[25] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[24] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[23] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[22] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[21] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[20] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[19] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[18] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[17] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[16] = _zz_writeBack_DBusSimplePlugin_rspFormated_2; // @[Literal.scala 87:17]
    _zz_writeBack_DBusSimplePlugin_rspFormated_3[15 : 0] = writeBack_DBusSimplePlugin_rspShifted[15 : 0]; // @[Literal.scala 99:91]
  end

  always @(*) begin
    case(switch_Misc_l226)
      2'b00 : begin
        writeBack_DBusSimplePlugin_rspFormated = _zz_writeBack_DBusSimplePlugin_rspFormated_1; // @[Misc.scala 239:22]
      end
      2'b01 : begin
        writeBack_DBusSimplePlugin_rspFormated = _zz_writeBack_DBusSimplePlugin_rspFormated_3; // @[Misc.scala 239:22]
      end
      default : begin
        writeBack_DBusSimplePlugin_rspFormated = writeBack_DBusSimplePlugin_rspShifted; // @[Misc.scala 235:22]
      end
    endcase
  end

  assign when_DBusSimplePlugin_l558 = (writeBack_arbitration_isValid && writeBack_MEMORY_ENABLE); // @[BaseType.scala 305:24]
  always @(*) begin
    CsrPlugin_privilege = 2'b11; // @[CsrPlugin.scala 682:15]
    if(CsrPlugin_forceMachineWire) begin
      CsrPlugin_privilege = 2'b11; // @[CsrPlugin.scala 684:40]
    end
  end

  assign CsrPlugin_misa_base = 2'b01;
  assign CsrPlugin_misa_extensions = 26'h0000042;
  assign CsrPlugin_mtvec_mode = 2'b01;
  assign CsrPlugin_mtvec_base = 30'h00000080;
  assign _zz_when_CsrPlugin_l1224 = (CsrPlugin_mip_MTIP && CsrPlugin_mie_MTIE); // @[BaseType.scala 305:24]
  assign _zz_when_CsrPlugin_l1224_1 = (CsrPlugin_mip_MSIP && CsrPlugin_mie_MSIE); // @[BaseType.scala 305:24]
  assign _zz_when_CsrPlugin_l1224_2 = (CsrPlugin_mip_MEIP && CsrPlugin_mie_MEIE); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1218 = (CsrPlugin_mstatus_MIE || (CsrPlugin_privilege < 2'b11)); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1224 = ((_zz_when_CsrPlugin_l1224 && 1'b1) && (! 1'b0)); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1224_1 = ((_zz_when_CsrPlugin_l1224_1 && 1'b1) && (! 1'b0)); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1224_2 = ((_zz_when_CsrPlugin_l1224_2 && 1'b1) && (! 1'b0)); // @[BaseType.scala 305:24]
  assign CsrPlugin_exception = 1'b0; // @[CsrPlugin.scala 1245:115]
  assign CsrPlugin_lastStageWasWfi = 1'b0; // @[CsrPlugin.scala 1246:152]
  assign CsrPlugin_pipelineLiberator_active = ((CsrPlugin_interrupt_valid && CsrPlugin_allowInterrupts) && decode_arbitration_isValid); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1257 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_CsrPlugin_l1257_1 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_CsrPlugin_l1257_2 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_CsrPlugin_l1262 = ((! CsrPlugin_pipelineLiberator_active) || decode_arbitration_removeIt); // @[BaseType.scala 305:24]
  always @(*) begin
    CsrPlugin_pipelineLiberator_done = CsrPlugin_pipelineLiberator_pcValids_2; // @[Misc.scala 552:9]
    if(CsrPlugin_hadException) begin
      CsrPlugin_pipelineLiberator_done = 1'b0; // @[CsrPlugin.scala 1277:39]
    end
  end

  assign CsrPlugin_interruptJump = ((CsrPlugin_interrupt_valid && CsrPlugin_pipelineLiberator_done) && CsrPlugin_allowInterrupts); // @[CsrPlugin.scala 1273:21]
  assign CsrPlugin_targetPrivilege = CsrPlugin_interrupt_targetPrivilege; // @[Misc.scala 552:9]
  assign CsrPlugin_trapCause = CsrPlugin_interrupt_code; // @[Misc.scala 552:9]
  assign CsrPlugin_trapCauseEbreakDebug = 1'b0; // @[CsrPlugin.scala 1291:34]
  always @(*) begin
    CsrPlugin_xtvec_mode = 2'bxx; // @[Bits.scala 231:20]
    case(CsrPlugin_targetPrivilege)
      2'b11 : begin
        CsrPlugin_xtvec_mode = CsrPlugin_mtvec_mode; // @[CsrPlugin.scala 1307:22]
      end
      default : begin
      end
    endcase
  end

  always @(*) begin
    CsrPlugin_xtvec_base = 30'bxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx; // @[UInt.scala 467:20]
    case(CsrPlugin_targetPrivilege)
      2'b11 : begin
        CsrPlugin_xtvec_base = CsrPlugin_mtvec_base; // @[CsrPlugin.scala 1307:22]
      end
      default : begin
      end
    endcase
  end

  assign CsrPlugin_trapEnterDebug = 1'b0; // @[CsrPlugin.scala 1310:28]
  assign when_CsrPlugin_l1312 = (CsrPlugin_hadException || CsrPlugin_interruptJump); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1320 = (! CsrPlugin_trapEnterDebug); // @[BaseType.scala 299:24]
  assign when_CsrPlugin_l1378 = (writeBack_arbitration_isValid && (writeBack_ENV_CTRL == EnvCtrlEnum_XRET)); // @[BaseType.scala 305:24]
  assign switch_CsrPlugin_l1382 = writeBack_INSTRUCTION[29 : 28]; // @[BaseType.scala 299:24]
  assign contextSwitching = CsrPlugin_jumpInterface_valid; // @[CsrPlugin.scala 1407:24]
  assign when_CsrPlugin_l1449 = (|{(writeBack_arbitration_isValid && (writeBack_ENV_CTRL == EnvCtrlEnum_XRET)),{(memory_arbitration_isValid && (memory_ENV_CTRL == EnvCtrlEnum_XRET)),(execute_arbitration_isValid && (execute_ENV_CTRL == EnvCtrlEnum_XRET))}}); // @[BaseType.scala 312:24]
  assign execute_CsrPlugin_blockedBySideEffects = ((|{writeBack_arbitration_isValid,memory_arbitration_isValid}) || 1'b0); // @[BaseType.scala 305:24]
  always @(*) begin
    execute_CsrPlugin_illegalAccess = 1'b1; // @[CsrPlugin.scala 1456:29]
    if(execute_CsrPlugin_csr_768) begin
      execute_CsrPlugin_illegalAccess = 1'b0; // @[CsrPlugin.scala 1531:29]
    end
    if(execute_CsrPlugin_csr_836) begin
      execute_CsrPlugin_illegalAccess = 1'b0; // @[CsrPlugin.scala 1531:29]
    end
    if(execute_CsrPlugin_csr_772) begin
      execute_CsrPlugin_illegalAccess = 1'b0; // @[CsrPlugin.scala 1531:29]
    end
    if(execute_CsrPlugin_csr_834) begin
      if(execute_CSR_READ_OPCODE) begin
        execute_CsrPlugin_illegalAccess = 1'b0; // @[CsrPlugin.scala 1534:52]
      end
    end
    if(CsrPlugin_csrMapping_allowCsrSignal) begin
      execute_CsrPlugin_illegalAccess = 1'b0; // @[CsrPlugin.scala 1622:25]
    end
    if(when_CsrPlugin_l1627) begin
      execute_CsrPlugin_illegalAccess = 1'b1; // @[CsrPlugin.scala 1628:27]
    end
    if(when_CsrPlugin_l1633) begin
      execute_CsrPlugin_illegalAccess = 1'b0; // @[CsrPlugin.scala 1633:25]
    end
  end

  always @(*) begin
    execute_CsrPlugin_illegalInstruction = 1'b0; // @[CsrPlugin.scala 1457:34]
    if(when_CsrPlugin_l1469) begin
      if(when_CsrPlugin_l1470) begin
        execute_CsrPlugin_illegalInstruction = 1'b1; // @[CsrPlugin.scala 1471:32]
      end
    end
  end

  assign when_CsrPlugin_l1469 = (execute_arbitration_isValid && (execute_ENV_CTRL == EnvCtrlEnum_XRET)); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1470 = (CsrPlugin_privilege < execute_INSTRUCTION[29 : 28]); // @[BaseType.scala 305:24]
  always @(*) begin
    execute_CsrPlugin_writeInstruction = ((execute_arbitration_isValid && execute_IS_CSR) && execute_CSR_WRITE_OPCODE); // @[BaseType.scala 305:24]
    if(when_CsrPlugin_l1627) begin
      execute_CsrPlugin_writeInstruction = 1'b0; // @[CsrPlugin.scala 1630:30]
    end
  end

  always @(*) begin
    execute_CsrPlugin_readInstruction = ((execute_arbitration_isValid && execute_IS_CSR) && execute_CSR_READ_OPCODE); // @[BaseType.scala 305:24]
    if(when_CsrPlugin_l1627) begin
      execute_CsrPlugin_readInstruction = 1'b0; // @[CsrPlugin.scala 1629:29]
    end
  end

  assign execute_CsrPlugin_writeEnable = (execute_CsrPlugin_writeInstruction && (! execute_arbitration_isStuck)); // @[BaseType.scala 305:24]
  assign execute_CsrPlugin_readEnable = (execute_CsrPlugin_readInstruction && (! execute_arbitration_isStuck)); // @[BaseType.scala 305:24]
  assign CsrPlugin_csrMapping_hazardFree = (! execute_CsrPlugin_blockedBySideEffects); // @[CsrPlugin.scala 1501:31]
  assign execute_CsrPlugin_readToWriteData = CsrPlugin_csrMapping_readDataSignal; // @[Misc.scala 552:9]
  assign switch_Misc_l226_1 = execute_INSTRUCTION[13]; // @[BaseType.scala 305:24]
  always @(*) begin
    case(switch_Misc_l226_1)
      1'b0 : begin
        _zz_CsrPlugin_csrMapping_writeDataSignal = execute_SRC1; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_CsrPlugin_csrMapping_writeDataSignal = (execute_INSTRUCTION[12] ? (execute_CsrPlugin_readToWriteData & (~ execute_SRC1)) : (execute_CsrPlugin_readToWriteData | execute_SRC1)); // @[Misc.scala 239:22]
      end
    endcase
  end

  assign CsrPlugin_csrMapping_writeDataSignal = _zz_CsrPlugin_csrMapping_writeDataSignal; // @[CsrPlugin.scala 1504:19]
  assign when_CsrPlugin_l1509 = (execute_arbitration_isValid && execute_IS_CSR); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1513 = (execute_arbitration_isValid && (execute_IS_CSR || 1'b0)); // @[BaseType.scala 305:24]
  assign execute_CsrPlugin_csrAddress = execute_INSTRUCTION[31 : 20]; // @[BaseType.scala 299:24]
  assign _zz_decode_SRC_LESS_UNSIGNED_1 = ((decode_INSTRUCTION & 32'h00004050) == 32'h00004050); // @[BaseType.scala 305:24]
  assign _zz_decode_SRC_LESS_UNSIGNED_2 = ((decode_INSTRUCTION & 32'h00006004) == 32'h00002000); // @[BaseType.scala 305:24]
  assign _zz_decode_SRC_LESS_UNSIGNED_3 = ((decode_INSTRUCTION & 32'h00000018) == 32'h0); // @[BaseType.scala 305:24]
  assign _zz_decode_SRC_LESS_UNSIGNED_4 = ((decode_INSTRUCTION & 32'h00000004) == 32'h00000004); // @[BaseType.scala 305:24]
  assign _zz_decode_SRC_LESS_UNSIGNED_5 = ((decode_INSTRUCTION & 32'h00000048) == 32'h00000048); // @[BaseType.scala 305:24]
  assign _zz_decode_SRC_LESS_UNSIGNED = {(|{_zz_decode_SRC_LESS_UNSIGNED_5,(_zz__zz_decode_SRC_LESS_UNSIGNED == _zz__zz_decode_SRC_LESS_UNSIGNED_1)}),{(|(_zz__zz_decode_SRC_LESS_UNSIGNED_2 == _zz__zz_decode_SRC_LESS_UNSIGNED_3)),{(|_zz__zz_decode_SRC_LESS_UNSIGNED_4),{(|_zz__zz_decode_SRC_LESS_UNSIGNED_5),{_zz__zz_decode_SRC_LESS_UNSIGNED_8,{_zz__zz_decode_SRC_LESS_UNSIGNED_13,_zz__zz_decode_SRC_LESS_UNSIGNED_16}}}}}}; // @[DecoderSimplePlugin.scala 161:19]
  assign _zz_decode_SRC1_CTRL_2 = _zz_decode_SRC_LESS_UNSIGNED[1 : 0]; // @[Enum.scala 186:17]
  assign _zz_decode_SRC1_CTRL_1 = _zz_decode_SRC1_CTRL_2; // @[Enum.scala 188:10]
  assign _zz_decode_SRC2_CTRL_2 = _zz_decode_SRC_LESS_UNSIGNED[6 : 5]; // @[Enum.scala 186:17]
  assign _zz_decode_SRC2_CTRL_1 = _zz_decode_SRC2_CTRL_2; // @[Enum.scala 188:10]
  assign _zz_decode_ENV_CTRL_2 = _zz_decode_SRC_LESS_UNSIGNED[14 : 14]; // @[Enum.scala 186:17]
  assign _zz_decode_ENV_CTRL_1 = _zz_decode_ENV_CTRL_2; // @[Enum.scala 188:10]
  assign _zz_decode_ALU_CTRL_2 = _zz_decode_SRC_LESS_UNSIGNED[16 : 15]; // @[Enum.scala 186:17]
  assign _zz_decode_ALU_CTRL_1 = _zz_decode_ALU_CTRL_2; // @[Enum.scala 188:10]
  assign _zz_decode_ALU_BITWISE_CTRL_2 = _zz_decode_SRC_LESS_UNSIGNED[19 : 18]; // @[Enum.scala 186:17]
  assign _zz_decode_ALU_BITWISE_CTRL_1 = _zz_decode_ALU_BITWISE_CTRL_2; // @[Enum.scala 188:10]
  assign _zz_decode_SHIFT_CTRL_2 = _zz_decode_SRC_LESS_UNSIGNED[22 : 21]; // @[Enum.scala 186:17]
  assign _zz_decode_SHIFT_CTRL_1 = _zz_decode_SHIFT_CTRL_2; // @[Enum.scala 188:10]
  assign _zz_decode_BRANCH_CTRL_2 = _zz_decode_SRC_LESS_UNSIGNED[24 : 23]; // @[Enum.scala 186:17]
  assign _zz_decode_BRANCH_CTRL_1 = _zz_decode_BRANCH_CTRL_2; // @[Enum.scala 188:10]
  assign when_RegFilePlugin_l63 = (decode_INSTRUCTION[11 : 7] == 5'h0); // @[BaseType.scala 305:24]
  assign decode_RegFilePlugin_regFileReadAddress1 = decode_INSTRUCTION_ANTICIPATED[19 : 15]; // @[BaseType.scala 318:22]
  assign decode_RegFilePlugin_regFileReadAddress2 = decode_INSTRUCTION_ANTICIPATED[24 : 20]; // @[BaseType.scala 318:22]
  assign decode_RegFilePlugin_rs1Data = _zz_RegFilePlugin_regFile_port0; // @[Bits.scala 133:56]
  assign decode_RegFilePlugin_rs2Data = _zz_RegFilePlugin_regFile_port1; // @[Bits.scala 133:56]
  always @(*) begin
    lastStageRegFileWrite_valid = (_zz_lastStageRegFileWrite_valid && writeBack_arbitration_isFiring); // @[RegFilePlugin.scala 102:26]
    if(_zz_2) begin
      lastStageRegFileWrite_valid = 1'b1; // @[RegFilePlugin.scala 114:28]
    end
  end

  always @(*) begin
    lastStageRegFileWrite_payload_address = _zz_lastStageRegFileWrite_payload_address[11 : 7]; // @[RegFilePlugin.scala 103:28]
    if(_zz_2) begin
      lastStageRegFileWrite_payload_address = 5'h0; // @[RegFilePlugin.scala 116:32]
    end
  end

  always @(*) begin
    lastStageRegFileWrite_payload_data = _zz_lastStageRegFileWrite_payload_data; // @[RegFilePlugin.scala 104:25]
    if(_zz_2) begin
      lastStageRegFileWrite_payload_data = 32'h0; // @[RegFilePlugin.scala 117:29]
    end
  end

  always @(*) begin
    case(execute_ALU_BITWISE_CTRL)
      AluBitwiseCtrlEnum_AND_1 : begin
        execute_IntAluPlugin_bitwise = (execute_SRC1 & execute_SRC2); // @[Misc.scala 239:22]
      end
      AluBitwiseCtrlEnum_OR_1 : begin
        execute_IntAluPlugin_bitwise = (execute_SRC1 | execute_SRC2); // @[Misc.scala 239:22]
      end
      default : begin
        execute_IntAluPlugin_bitwise = (execute_SRC1 ^ execute_SRC2); // @[Misc.scala 239:22]
      end
    endcase
  end

  always @(*) begin
    case(execute_ALU_CTRL)
      AluCtrlEnum_BITWISE : begin
        _zz_execute_REGFILE_WRITE_DATA = execute_IntAluPlugin_bitwise; // @[Misc.scala 239:22]
      end
      AluCtrlEnum_SLT_SLTU : begin
        _zz_execute_REGFILE_WRITE_DATA = {31'd0, _zz__zz_execute_REGFILE_WRITE_DATA}; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_execute_REGFILE_WRITE_DATA = execute_SRC_ADD_SUB; // @[Misc.scala 239:22]
      end
    endcase
  end

  always @(*) begin
    case(decode_SRC1_CTRL)
      Src1CtrlEnum_RS : begin
        _zz_decode_SRC1 = _zz_decode_to_execute_RS1; // @[Misc.scala 239:22]
      end
      Src1CtrlEnum_PC_INCREMENT : begin
        _zz_decode_SRC1 = {29'd0, _zz__zz_decode_SRC1}; // @[Misc.scala 239:22]
      end
      Src1CtrlEnum_IMU : begin
        _zz_decode_SRC1 = {decode_INSTRUCTION[31 : 12],12'h0}; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_decode_SRC1 = {27'd0, _zz__zz_decode_SRC1_1}; // @[Misc.scala 239:22]
      end
    endcase
  end

  assign _zz_decode_SRC2 = decode_INSTRUCTION[31]; // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_decode_SRC2_1[19] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[18] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[17] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[16] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[15] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[14] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[13] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[12] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[11] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[10] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[9] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[8] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[7] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[6] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[5] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[4] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[3] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[2] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[1] = _zz_decode_SRC2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_1[0] = _zz_decode_SRC2; // @[Literal.scala 87:17]
  end

  assign _zz_decode_SRC2_2 = _zz__zz_decode_SRC2_2[11]; // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_decode_SRC2_3[19] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[18] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[17] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[16] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[15] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[14] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[13] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[12] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[11] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[10] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[9] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[8] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[7] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[6] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[5] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[4] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[3] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[2] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[1] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
    _zz_decode_SRC2_3[0] = _zz_decode_SRC2_2; // @[Literal.scala 87:17]
  end

  always @(*) begin
    case(decode_SRC2_CTRL)
      Src2CtrlEnum_RS : begin
        _zz_decode_SRC2_4 = _zz_decode_to_execute_RS2; // @[Misc.scala 239:22]
      end
      Src2CtrlEnum_IMI : begin
        _zz_decode_SRC2_4 = {_zz_decode_SRC2_1,decode_INSTRUCTION[31 : 20]}; // @[Misc.scala 239:22]
      end
      Src2CtrlEnum_IMS : begin
        _zz_decode_SRC2_4 = {_zz_decode_SRC2_3,{decode_INSTRUCTION[31 : 25],decode_INSTRUCTION[11 : 7]}}; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_decode_SRC2_4 = _zz_decode_to_execute_PC; // @[Misc.scala 239:22]
      end
    endcase
  end

  always @(*) begin
    execute_SrcPlugin_addSub = _zz_execute_SrcPlugin_addSub; // @[BaseType.scala 318:22]
    if(execute_SRC2_FORCE_ZERO) begin
      execute_SrcPlugin_addSub = execute_SRC1; // @[SrcPlugin.scala 69:46]
    end
  end

  assign execute_SrcPlugin_less = ((execute_SRC1[31] == execute_SRC2[31]) ? execute_SrcPlugin_addSub[31] : (execute_SRC_LESS_UNSIGNED ? execute_SRC2[31] : execute_SRC1[31])); // @[Expression.scala 1420:25]
  assign execute_LightShifterPlugin_isShift = (execute_SHIFT_CTRL != ShiftCtrlEnum_DISABLE_1); // @[BaseType.scala 305:24]
  assign execute_LightShifterPlugin_amplitude = (execute_LightShifterPlugin_isActive ? execute_LightShifterPlugin_amplitudeReg : execute_SRC2[4 : 0]); // @[Expression.scala 1420:25]
  assign execute_LightShifterPlugin_shiftInput = (execute_LightShifterPlugin_isActive ? memory_REGFILE_WRITE_DATA : execute_SRC1); // @[Expression.scala 1420:25]
  assign execute_LightShifterPlugin_done = (execute_LightShifterPlugin_amplitude[4 : 1] == 4'b0000); // @[BaseType.scala 305:24]
  assign when_ShiftPlugins_l169 = ((execute_arbitration_isValid && execute_LightShifterPlugin_isShift) && (execute_SRC2[4 : 0] != 5'h0)); // @[BaseType.scala 305:24]
  always @(*) begin
    case(execute_SHIFT_CTRL)
      ShiftCtrlEnum_SLL_1 : begin
        _zz_execute_to_memory_REGFILE_WRITE_DATA_1 = (execute_LightShifterPlugin_shiftInput <<< 1); // @[Misc.scala 239:22]
      end
      default : begin
        _zz_execute_to_memory_REGFILE_WRITE_DATA_1 = _zz__zz_execute_to_memory_REGFILE_WRITE_DATA_1; // @[Misc.scala 235:22]
      end
    endcase
  end

  assign when_ShiftPlugins_l175 = (! execute_arbitration_isStuckByOthers); // @[BaseType.scala 299:24]
  assign when_ShiftPlugins_l184 = (! execute_LightShifterPlugin_done); // @[BaseType.scala 299:24]
  always @(*) begin
    HazardSimplePlugin_src0Hazard = 1'b0; // @[HazardSimplePlugin.scala 36:24]
    if(HazardSimplePlugin_writeBackBuffer_valid) begin
      if(HazardSimplePlugin_addr0Match) begin
        HazardSimplePlugin_src0Hazard = 1'b1; // @[HazardSimplePlugin.scala 91:24]
      end
    end
    if(when_HazardSimplePlugin_l57) begin
      if(when_HazardSimplePlugin_l58) begin
        if(when_HazardSimplePlugin_l59) begin
          HazardSimplePlugin_src0Hazard = 1'b1; // @[HazardSimplePlugin.scala 60:26]
        end
      end
    end
    if(when_HazardSimplePlugin_l57_1) begin
      if(when_HazardSimplePlugin_l58_1) begin
        if(when_HazardSimplePlugin_l59_1) begin
          HazardSimplePlugin_src0Hazard = 1'b1; // @[HazardSimplePlugin.scala 60:26]
        end
      end
    end
    if(when_HazardSimplePlugin_l57_2) begin
      if(when_HazardSimplePlugin_l58_2) begin
        if(when_HazardSimplePlugin_l59_2) begin
          HazardSimplePlugin_src0Hazard = 1'b1; // @[HazardSimplePlugin.scala 60:26]
        end
      end
    end
    if(when_HazardSimplePlugin_l105) begin
      HazardSimplePlugin_src0Hazard = 1'b0; // @[HazardSimplePlugin.scala 106:22]
    end
  end

  always @(*) begin
    HazardSimplePlugin_src1Hazard = 1'b0; // @[HazardSimplePlugin.scala 37:24]
    if(HazardSimplePlugin_writeBackBuffer_valid) begin
      if(HazardSimplePlugin_addr1Match) begin
        HazardSimplePlugin_src1Hazard = 1'b1; // @[HazardSimplePlugin.scala 94:24]
      end
    end
    if(when_HazardSimplePlugin_l57) begin
      if(when_HazardSimplePlugin_l58) begin
        if(when_HazardSimplePlugin_l62) begin
          HazardSimplePlugin_src1Hazard = 1'b1; // @[HazardSimplePlugin.scala 63:26]
        end
      end
    end
    if(when_HazardSimplePlugin_l57_1) begin
      if(when_HazardSimplePlugin_l58_1) begin
        if(when_HazardSimplePlugin_l62_1) begin
          HazardSimplePlugin_src1Hazard = 1'b1; // @[HazardSimplePlugin.scala 63:26]
        end
      end
    end
    if(when_HazardSimplePlugin_l57_2) begin
      if(when_HazardSimplePlugin_l58_2) begin
        if(when_HazardSimplePlugin_l62_2) begin
          HazardSimplePlugin_src1Hazard = 1'b1; // @[HazardSimplePlugin.scala 63:26]
        end
      end
    end
    if(when_HazardSimplePlugin_l108) begin
      HazardSimplePlugin_src1Hazard = 1'b0; // @[HazardSimplePlugin.scala 109:22]
    end
  end

  assign HazardSimplePlugin_writeBackWrites_valid = (_zz_lastStageRegFileWrite_valid && writeBack_arbitration_isFiring); // @[HazardSimplePlugin.scala 74:29]
  assign HazardSimplePlugin_writeBackWrites_payload_address = _zz_lastStageRegFileWrite_payload_address[11 : 7]; // @[HazardSimplePlugin.scala 75:31]
  assign HazardSimplePlugin_writeBackWrites_payload_data = _zz_lastStageRegFileWrite_payload_data; // @[HazardSimplePlugin.scala 76:28]
  assign HazardSimplePlugin_addr0Match = (HazardSimplePlugin_writeBackBuffer_payload_address == decode_INSTRUCTION[19 : 15]); // @[BaseType.scala 305:24]
  assign HazardSimplePlugin_addr1Match = (HazardSimplePlugin_writeBackBuffer_payload_address == decode_INSTRUCTION[24 : 20]); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l59 = (writeBack_INSTRUCTION[11 : 7] == decode_INSTRUCTION[19 : 15]); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l62 = (writeBack_INSTRUCTION[11 : 7] == decode_INSTRUCTION[24 : 20]); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l57 = (writeBack_arbitration_isValid && writeBack_REGFILE_WRITE_VALID); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l58 = (1'b1 || (! 1'b1)); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l59_1 = (memory_INSTRUCTION[11 : 7] == decode_INSTRUCTION[19 : 15]); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l62_1 = (memory_INSTRUCTION[11 : 7] == decode_INSTRUCTION[24 : 20]); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l57_1 = (memory_arbitration_isValid && memory_REGFILE_WRITE_VALID); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l58_1 = (1'b1 || (! memory_BYPASSABLE_MEMORY_STAGE)); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l59_2 = (execute_INSTRUCTION[11 : 7] == decode_INSTRUCTION[19 : 15]); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l62_2 = (execute_INSTRUCTION[11 : 7] == decode_INSTRUCTION[24 : 20]); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l57_2 = (execute_arbitration_isValid && execute_REGFILE_WRITE_VALID); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l58_2 = (1'b1 || (! execute_BYPASSABLE_EXECUTE_STAGE)); // @[BaseType.scala 305:24]
  assign when_HazardSimplePlugin_l105 = (! decode_RS1_USE); // @[BaseType.scala 299:24]
  assign when_HazardSimplePlugin_l108 = (! decode_RS2_USE); // @[BaseType.scala 299:24]
  assign when_HazardSimplePlugin_l113 = (decode_arbitration_isValid && (HazardSimplePlugin_src0Hazard || HazardSimplePlugin_src1Hazard)); // @[BaseType.scala 305:24]
  assign execute_BranchPlugin_eq = (execute_SRC1 == execute_SRC2); // @[BaseType.scala 305:24]
  assign switch_Misc_l226_2 = execute_INSTRUCTION[14 : 12]; // @[BaseType.scala 299:24]
  always @(*) begin
    casez(switch_Misc_l226_2)
      3'b000 : begin
        _zz_execute_BRANCH_DO = execute_BranchPlugin_eq; // @[Misc.scala 239:22]
      end
      3'b001 : begin
        _zz_execute_BRANCH_DO = (! execute_BranchPlugin_eq); // @[Misc.scala 239:22]
      end
      3'b1?1 : begin
        _zz_execute_BRANCH_DO = (! execute_SRC_LESS); // @[Misc.scala 239:22]
      end
      default : begin
        _zz_execute_BRANCH_DO = execute_SRC_LESS; // @[Misc.scala 235:22]
      end
    endcase
  end

  always @(*) begin
    case(execute_BRANCH_CTRL)
      BranchCtrlEnum_INC : begin
        _zz_execute_BRANCH_DO_1 = 1'b0; // @[Misc.scala 239:22]
      end
      BranchCtrlEnum_JAL : begin
        _zz_execute_BRANCH_DO_1 = 1'b1; // @[Misc.scala 239:22]
      end
      BranchCtrlEnum_JALR : begin
        _zz_execute_BRANCH_DO_1 = 1'b1; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_execute_BRANCH_DO_1 = _zz_execute_BRANCH_DO; // @[Misc.scala 239:22]
      end
    endcase
  end

  assign execute_BranchPlugin_branch_src1 = ((execute_BRANCH_CTRL == BranchCtrlEnum_JALR) ? execute_RS1 : execute_PC); // @[Expression.scala 1420:25]
  assign _zz_execute_BranchPlugin_branch_src2 = _zz__zz_execute_BranchPlugin_branch_src2[19]; // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_execute_BranchPlugin_branch_src2_1[10] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[9] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[8] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[7] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[6] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[5] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[4] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[3] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[2] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[1] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_1[0] = _zz_execute_BranchPlugin_branch_src2; // @[Literal.scala 87:17]
  end

  assign _zz_execute_BranchPlugin_branch_src2_2 = execute_INSTRUCTION[31]; // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_execute_BranchPlugin_branch_src2_3[19] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[18] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[17] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[16] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[15] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[14] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[13] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[12] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[11] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[10] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[9] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[8] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[7] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[6] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[5] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[4] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[3] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[2] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[1] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_3[0] = _zz_execute_BranchPlugin_branch_src2_2; // @[Literal.scala 87:17]
  end

  assign _zz_execute_BranchPlugin_branch_src2_4 = _zz__zz_execute_BranchPlugin_branch_src2_4[11]; // @[BaseType.scala 305:24]
  always @(*) begin
    _zz_execute_BranchPlugin_branch_src2_5[18] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[17] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[16] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[15] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[14] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[13] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[12] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[11] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[10] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[9] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[8] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[7] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[6] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[5] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[4] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[3] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[2] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[1] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
    _zz_execute_BranchPlugin_branch_src2_5[0] = _zz_execute_BranchPlugin_branch_src2_4; // @[Literal.scala 87:17]
  end

  always @(*) begin
    case(execute_BRANCH_CTRL)
      BranchCtrlEnum_JAL : begin
        _zz_execute_BranchPlugin_branch_src2_6 = {{_zz_execute_BranchPlugin_branch_src2_1,{{{execute_INSTRUCTION[31],execute_INSTRUCTION[19 : 12]},execute_INSTRUCTION[20]},execute_INSTRUCTION[30 : 21]}},1'b0}; // @[Misc.scala 239:22]
      end
      BranchCtrlEnum_JALR : begin
        _zz_execute_BranchPlugin_branch_src2_6 = {_zz_execute_BranchPlugin_branch_src2_3,execute_INSTRUCTION[31 : 20]}; // @[Misc.scala 239:22]
      end
      default : begin
        _zz_execute_BranchPlugin_branch_src2_6 = {{_zz_execute_BranchPlugin_branch_src2_5,{{{execute_INSTRUCTION[31],execute_INSTRUCTION[7]},execute_INSTRUCTION[30 : 25]},execute_INSTRUCTION[11 : 8]}},1'b0}; // @[Misc.scala 235:22]
      end
    endcase
  end

  assign execute_BranchPlugin_branch_src2 = _zz_execute_BranchPlugin_branch_src2_6; // @[BaseType.scala 318:22]
  assign execute_BranchPlugin_branchAdder = (execute_BranchPlugin_branch_src1 + execute_BranchPlugin_branch_src2); // @[BaseType.scala 299:24]
  assign BranchPlugin_jumpInterface_valid = ((memory_arbitration_isValid && memory_BRANCH_DO) && (! 1'b0)); // @[BranchPlugin.scala 213:27]
  assign BranchPlugin_jumpInterface_payload = memory_BRANCH_CALC; // @[BranchPlugin.scala 214:29]
  assign when_Pipeline_l124 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_1 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_2 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_3 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_4 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_5 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_6 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_7 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_8 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_9 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_10 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_decode_SRC1_CTRL = _zz_decode_SRC1_CTRL_1; // @[Pipeline.scala 121:26]
  assign when_Pipeline_l124_11 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_12 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_13 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_14 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_decode_SRC2_CTRL = _zz_decode_SRC2_CTRL_1; // @[Pipeline.scala 121:26]
  assign when_Pipeline_l124_15 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_16 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_17 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_18 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_19 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_20 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_21 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_22 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_23 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_decode_to_execute_ENV_CTRL_1 = decode_ENV_CTRL; // @[Pipeline.scala 110:25]
  assign _zz_execute_to_memory_ENV_CTRL_1 = execute_ENV_CTRL; // @[Pipeline.scala 110:25]
  assign _zz_memory_to_writeBack_ENV_CTRL_1 = memory_ENV_CTRL; // @[Pipeline.scala 110:25]
  assign _zz_decode_ENV_CTRL = _zz_decode_ENV_CTRL_1; // @[Pipeline.scala 121:26]
  assign when_Pipeline_l124_24 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_execute_ENV_CTRL = decode_to_execute_ENV_CTRL; // @[Pipeline.scala 124:26]
  assign when_Pipeline_l124_25 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_memory_ENV_CTRL = execute_to_memory_ENV_CTRL; // @[Pipeline.scala 124:26]
  assign when_Pipeline_l124_26 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_writeBack_ENV_CTRL = memory_to_writeBack_ENV_CTRL; // @[Pipeline.scala 124:26]
  assign _zz_decode_to_execute_ALU_CTRL_1 = decode_ALU_CTRL; // @[Pipeline.scala 110:25]
  assign _zz_decode_ALU_CTRL = _zz_decode_ALU_CTRL_1; // @[Pipeline.scala 121:26]
  assign when_Pipeline_l124_27 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_execute_ALU_CTRL = decode_to_execute_ALU_CTRL; // @[Pipeline.scala 124:26]
  assign when_Pipeline_l124_28 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_decode_to_execute_ALU_BITWISE_CTRL_1 = decode_ALU_BITWISE_CTRL; // @[Pipeline.scala 110:25]
  assign _zz_decode_ALU_BITWISE_CTRL = _zz_decode_ALU_BITWISE_CTRL_1; // @[Pipeline.scala 121:26]
  assign when_Pipeline_l124_29 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_execute_ALU_BITWISE_CTRL = decode_to_execute_ALU_BITWISE_CTRL; // @[Pipeline.scala 124:26]
  assign _zz_decode_to_execute_SHIFT_CTRL_1 = decode_SHIFT_CTRL; // @[Pipeline.scala 110:25]
  assign _zz_decode_SHIFT_CTRL = _zz_decode_SHIFT_CTRL_1; // @[Pipeline.scala 121:26]
  assign when_Pipeline_l124_30 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_execute_SHIFT_CTRL = decode_to_execute_SHIFT_CTRL; // @[Pipeline.scala 124:26]
  assign _zz_decode_to_execute_BRANCH_CTRL_1 = decode_BRANCH_CTRL; // @[Pipeline.scala 110:25]
  assign _zz_decode_BRANCH_CTRL = _zz_decode_BRANCH_CTRL_1; // @[Pipeline.scala 121:26]
  assign when_Pipeline_l124_31 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign _zz_execute_BRANCH_CTRL = decode_to_execute_BRANCH_CTRL; // @[Pipeline.scala 124:26]
  assign when_Pipeline_l124_32 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_33 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_34 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_35 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_36 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_37 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_38 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_39 = ((! memory_arbitration_isStuck) && (! execute_arbitration_isStuckByOthers)); // @[BaseType.scala 305:24]
  assign when_Pipeline_l124_40 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_41 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_42 = (! memory_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_Pipeline_l124_43 = (! writeBack_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign decode_arbitration_isFlushed = (({writeBack_arbitration_flushNext,{memory_arbitration_flushNext,execute_arbitration_flushNext}} != 3'b000) || ({writeBack_arbitration_flushIt,{memory_arbitration_flushIt,{execute_arbitration_flushIt,decode_arbitration_flushIt}}} != 4'b0000)); // @[Pipeline.scala 132:35]
  assign execute_arbitration_isFlushed = (({writeBack_arbitration_flushNext,memory_arbitration_flushNext} != 2'b00) || ({writeBack_arbitration_flushIt,{memory_arbitration_flushIt,execute_arbitration_flushIt}} != 3'b000)); // @[Pipeline.scala 132:35]
  assign memory_arbitration_isFlushed = ((writeBack_arbitration_flushNext != 1'b0) || ({writeBack_arbitration_flushIt,memory_arbitration_flushIt} != 2'b00)); // @[Pipeline.scala 132:35]
  assign writeBack_arbitration_isFlushed = (1'b0 || (writeBack_arbitration_flushIt != 1'b0)); // @[Pipeline.scala 132:35]
  assign decode_arbitration_isStuckByOthers = (decode_arbitration_haltByOther || (((1'b0 || execute_arbitration_isStuck) || memory_arbitration_isStuck) || writeBack_arbitration_isStuck)); // @[Pipeline.scala 141:41]
  assign decode_arbitration_isStuck = (decode_arbitration_haltItself || decode_arbitration_isStuckByOthers); // @[Pipeline.scala 142:33]
  assign decode_arbitration_isMoving = ((! decode_arbitration_isStuck) && (! decode_arbitration_removeIt)); // @[Pipeline.scala 143:34]
  assign decode_arbitration_isFiring = ((decode_arbitration_isValid && (! decode_arbitration_isStuck)) && (! decode_arbitration_removeIt)); // @[Pipeline.scala 144:34]
  assign execute_arbitration_isStuckByOthers = (execute_arbitration_haltByOther || ((1'b0 || memory_arbitration_isStuck) || writeBack_arbitration_isStuck)); // @[Pipeline.scala 141:41]
  assign execute_arbitration_isStuck = (execute_arbitration_haltItself || execute_arbitration_isStuckByOthers); // @[Pipeline.scala 142:33]
  assign execute_arbitration_isMoving = ((! execute_arbitration_isStuck) && (! execute_arbitration_removeIt)); // @[Pipeline.scala 143:34]
  assign execute_arbitration_isFiring = ((execute_arbitration_isValid && (! execute_arbitration_isStuck)) && (! execute_arbitration_removeIt)); // @[Pipeline.scala 144:34]
  assign memory_arbitration_isStuckByOthers = (memory_arbitration_haltByOther || (1'b0 || writeBack_arbitration_isStuck)); // @[Pipeline.scala 141:41]
  assign memory_arbitration_isStuck = (memory_arbitration_haltItself || memory_arbitration_isStuckByOthers); // @[Pipeline.scala 142:33]
  assign memory_arbitration_isMoving = ((! memory_arbitration_isStuck) && (! memory_arbitration_removeIt)); // @[Pipeline.scala 143:34]
  assign memory_arbitration_isFiring = ((memory_arbitration_isValid && (! memory_arbitration_isStuck)) && (! memory_arbitration_removeIt)); // @[Pipeline.scala 144:34]
  assign writeBack_arbitration_isStuckByOthers = (writeBack_arbitration_haltByOther || 1'b0); // @[Pipeline.scala 141:41]
  assign writeBack_arbitration_isStuck = (writeBack_arbitration_haltItself || writeBack_arbitration_isStuckByOthers); // @[Pipeline.scala 142:33]
  assign writeBack_arbitration_isMoving = ((! writeBack_arbitration_isStuck) && (! writeBack_arbitration_removeIt)); // @[Pipeline.scala 143:34]
  assign writeBack_arbitration_isFiring = ((writeBack_arbitration_isValid && (! writeBack_arbitration_isStuck)) && (! writeBack_arbitration_removeIt)); // @[Pipeline.scala 144:34]
  assign when_Pipeline_l151 = ((! execute_arbitration_isStuck) || execute_arbitration_removeIt); // @[BaseType.scala 305:24]
  assign when_Pipeline_l154 = ((! decode_arbitration_isStuck) && (! decode_arbitration_removeIt)); // @[BaseType.scala 305:24]
  assign when_Pipeline_l151_1 = ((! memory_arbitration_isStuck) || memory_arbitration_removeIt); // @[BaseType.scala 305:24]
  assign when_Pipeline_l154_1 = ((! execute_arbitration_isStuck) && (! execute_arbitration_removeIt)); // @[BaseType.scala 305:24]
  assign when_Pipeline_l151_2 = ((! writeBack_arbitration_isStuck) || writeBack_arbitration_removeIt); // @[BaseType.scala 305:24]
  assign when_Pipeline_l154_2 = ((! memory_arbitration_isStuck) && (! memory_arbitration_removeIt)); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1591 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_CsrPlugin_l1591_1 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_CsrPlugin_l1591_2 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign when_CsrPlugin_l1591_3 = (! execute_arbitration_isStuck); // @[BaseType.scala 299:24]
  assign switch_CsrPlugin_l982 = CsrPlugin_csrMapping_writeDataSignal[12 : 11]; // @[BaseType.scala 299:24]
  always @(*) begin
    _zz_CsrPlugin_csrMapping_readDataInit = 32'h0; // @[Expression.scala 2301:18]
    if(execute_CsrPlugin_csr_768) begin
      _zz_CsrPlugin_csrMapping_readDataInit[7 : 7] = CsrPlugin_mstatus_MPIE; // @[CsrPlugin.scala 1600:138]
      _zz_CsrPlugin_csrMapping_readDataInit[3 : 3] = CsrPlugin_mstatus_MIE; // @[CsrPlugin.scala 1600:138]
      _zz_CsrPlugin_csrMapping_readDataInit[12 : 11] = CsrPlugin_mstatus_MPP; // @[CsrPlugin.scala 1600:138]
    end
  end

  always @(*) begin
    _zz_CsrPlugin_csrMapping_readDataInit_1 = 32'h0; // @[Expression.scala 2301:18]
    if(execute_CsrPlugin_csr_836) begin
      _zz_CsrPlugin_csrMapping_readDataInit_1[11 : 11] = CsrPlugin_mip_MEIP; // @[CsrPlugin.scala 1600:138]
      _zz_CsrPlugin_csrMapping_readDataInit_1[7 : 7] = CsrPlugin_mip_MTIP; // @[CsrPlugin.scala 1600:138]
      _zz_CsrPlugin_csrMapping_readDataInit_1[3 : 3] = CsrPlugin_mip_MSIP; // @[CsrPlugin.scala 1600:138]
    end
  end

  always @(*) begin
    _zz_CsrPlugin_csrMapping_readDataInit_2 = 32'h0; // @[Expression.scala 2301:18]
    if(execute_CsrPlugin_csr_772) begin
      _zz_CsrPlugin_csrMapping_readDataInit_2[11 : 11] = CsrPlugin_mie_MEIE; // @[CsrPlugin.scala 1600:138]
      _zz_CsrPlugin_csrMapping_readDataInit_2[7 : 7] = CsrPlugin_mie_MTIE; // @[CsrPlugin.scala 1600:138]
      _zz_CsrPlugin_csrMapping_readDataInit_2[3 : 3] = CsrPlugin_mie_MSIE; // @[CsrPlugin.scala 1600:138]
    end
  end

  always @(*) begin
    _zz_CsrPlugin_csrMapping_readDataInit_3 = 32'h0; // @[Expression.scala 2301:18]
    if(execute_CsrPlugin_csr_834) begin
      _zz_CsrPlugin_csrMapping_readDataInit_3[31 : 31] = CsrPlugin_mcause_interrupt; // @[CsrPlugin.scala 1600:138]
      _zz_CsrPlugin_csrMapping_readDataInit_3[3 : 0] = CsrPlugin_mcause_exceptionCode; // @[CsrPlugin.scala 1600:138]
    end
  end

  assign CsrPlugin_csrMapping_readDataInit = ((_zz_CsrPlugin_csrMapping_readDataInit | _zz_CsrPlugin_csrMapping_readDataInit_1) | (_zz_CsrPlugin_csrMapping_readDataInit_2 | _zz_CsrPlugin_csrMapping_readDataInit_3)); // @[CsrPlugin.scala 1606:39]
  always @(*) begin
    when_CsrPlugin_l1627 = 1'b0; // @[CsrPlugin.scala 1624:27]
    if(when_CsrPlugin_l1625) begin
      when_CsrPlugin_l1627 = 1'b1; // @[CsrPlugin.scala 1625:21]
    end
  end

  assign when_CsrPlugin_l1625 = (CsrPlugin_privilege < execute_CsrPlugin_csrAddress[9 : 8]); // @[BaseType.scala 305:24]
  assign when_CsrPlugin_l1633 = ((! execute_arbitration_isValid) || (! execute_IS_CSR)); // @[BaseType.scala 305:24]
  always @(posedge clk) begin
    if(reset) begin
      IBusSimplePlugin_fetchPc_pcReg <= 32'h0; // @[Data.scala 400:33]
      IBusSimplePlugin_fetchPc_correctionReg <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_fetchPc_booted <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_fetchPc_inc <= 1'b0; // @[Data.scala 400:33]
      _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid_1 <= 1'b0; // @[Data.scala 400:33]
      _zz_IBusSimplePlugin_injector_decodeInput_valid <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_injector_nextPcCalc_valids_0 <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_injector_nextPcCalc_valids_1 <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_injector_nextPcCalc_valids_2 <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_injector_nextPcCalc_valids_3 <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_injector_nextPcCalc_valids_4 <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_cmd_rValid <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_pending_value <= 1'b0; // @[Data.scala 400:33]
      IBusSimplePlugin_rspJoin_rspBuffer_discardCounter <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_mstatus_MIE <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_mstatus_MPIE <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_mstatus_MPP <= 2'b11; // @[Data.scala 400:33]
      CsrPlugin_mie_MEIE <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_mie_MTIE <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_mie_MSIE <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_mcycle <= 64'h0; // @[Data.scala 400:33]
      CsrPlugin_minstret <= 64'h0; // @[Data.scala 400:33]
      CsrPlugin_interrupt_valid <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_pipelineLiberator_pcValids_0 <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_pipelineLiberator_pcValids_1 <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_pipelineLiberator_pcValids_2 <= 1'b0; // @[Data.scala 400:33]
      CsrPlugin_hadException <= 1'b0; // @[Data.scala 400:33]
      execute_CsrPlugin_wfiWake <= 1'b0; // @[Data.scala 400:33]
      _zz_2 <= 1'b1; // @[Data.scala 400:33]
      execute_LightShifterPlugin_isActive <= 1'b0; // @[Data.scala 400:33]
      HazardSimplePlugin_writeBackBuffer_valid <= 1'b0; // @[Data.scala 400:33]
      execute_arbitration_isValid <= 1'b0; // @[Data.scala 400:33]
      memory_arbitration_isValid <= 1'b0; // @[Data.scala 400:33]
      writeBack_arbitration_isValid <= 1'b0; // @[Data.scala 400:33]
    end else begin
      if(IBusSimplePlugin_fetchPc_correction) begin
        IBusSimplePlugin_fetchPc_correctionReg <= 1'b1; // @[Fetcher.scala 130:42]
      end
      if(IBusSimplePlugin_fetchPc_output_fire) begin
        IBusSimplePlugin_fetchPc_correctionReg <= 1'b0; // @[Fetcher.scala 130:62]
      end
      IBusSimplePlugin_fetchPc_booted <= 1'b1; // @[Reg.scala 39:30]
      if(when_Fetcher_l134) begin
        IBusSimplePlugin_fetchPc_inc <= 1'b0; // @[Fetcher.scala 134:32]
      end
      if(IBusSimplePlugin_fetchPc_output_fire_1) begin
        IBusSimplePlugin_fetchPc_inc <= 1'b1; // @[Fetcher.scala 134:72]
      end
      if(when_Fetcher_l134_1) begin
        IBusSimplePlugin_fetchPc_inc <= 1'b0; // @[Fetcher.scala 134:93]
      end
      if(when_Fetcher_l161) begin
        IBusSimplePlugin_fetchPc_pcReg <= IBusSimplePlugin_fetchPc_pc; // @[Fetcher.scala 162:15]
      end
      if(IBusSimplePlugin_iBusRsp_flush) begin
        _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid_1 <= 1'b0; // @[Misc.scala 146:41]
      end
      if(_zz_IBusSimplePlugin_iBusRsp_stages_0_output_ready) begin
        _zz_IBusSimplePlugin_iBusRsp_stages_1_input_valid_1 <= (IBusSimplePlugin_iBusRsp_stages_0_output_valid && (! 1'b0)); // @[Misc.scala 154:18]
      end
      if(decode_arbitration_removeIt) begin
        _zz_IBusSimplePlugin_injector_decodeInput_valid <= 1'b0; // @[Misc.scala 146:41]
      end
      if(IBusSimplePlugin_iBusRsp_output_ready) begin
        _zz_IBusSimplePlugin_injector_decodeInput_valid <= (IBusSimplePlugin_iBusRsp_output_valid && (! IBusSimplePlugin_externalFlush)); // @[Misc.scala 154:18]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_0 <= 1'b0; // @[Fetcher.scala 330:17]
      end
      if(when_Fetcher_l332) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_0 <= 1'b1; // @[Fetcher.scala 333:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_1 <= 1'b0; // @[Fetcher.scala 330:17]
      end
      if(when_Fetcher_l332_1) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_1 <= IBusSimplePlugin_injector_nextPcCalc_valids_0; // @[Fetcher.scala 333:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_1 <= 1'b0; // @[Fetcher.scala 336:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_2 <= 1'b0; // @[Fetcher.scala 330:17]
      end
      if(when_Fetcher_l332_2) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_2 <= IBusSimplePlugin_injector_nextPcCalc_valids_1; // @[Fetcher.scala 333:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_2 <= 1'b0; // @[Fetcher.scala 336:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_3 <= 1'b0; // @[Fetcher.scala 330:17]
      end
      if(when_Fetcher_l332_3) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_3 <= IBusSimplePlugin_injector_nextPcCalc_valids_2; // @[Fetcher.scala 333:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_3 <= 1'b0; // @[Fetcher.scala 336:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_4 <= 1'b0; // @[Fetcher.scala 330:17]
      end
      if(when_Fetcher_l332_4) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_4 <= IBusSimplePlugin_injector_nextPcCalc_valids_3; // @[Fetcher.scala 333:17]
      end
      if(IBusSimplePlugin_fetchPc_flushed) begin
        IBusSimplePlugin_injector_nextPcCalc_valids_4 <= 1'b0; // @[Fetcher.scala 336:17]
      end
      if(IBusSimplePlugin_cmd_valid) begin
        IBusSimplePlugin_cmd_rValid <= 1'b1; // @[Stream.scala 377:33]
      end
      if(IBusSimplePlugin_cmd_s2mPipe_ready) begin
        IBusSimplePlugin_cmd_rValid <= 1'b0; // @[Stream.scala 377:53]
      end
      IBusSimplePlugin_pending_value <= IBusSimplePlugin_pending_next; // @[IBusSimplePlugin.scala 295:15]
      IBusSimplePlugin_rspJoin_rspBuffer_discardCounter <= (IBusSimplePlugin_rspJoin_rspBuffer_discardCounter - (IBusSimplePlugin_rspJoin_rspBuffer_c_io_pop_valid && (IBusSimplePlugin_rspJoin_rspBuffer_discardCounter != 1'b0))); // @[IBusSimplePlugin.scala 357:26]
      if(IBusSimplePlugin_iBusRsp_flush) begin
        IBusSimplePlugin_rspJoin_rspBuffer_discardCounter <= (IBusSimplePlugin_pending_value - IBusSimplePlugin_pending_dec); // @[IBusSimplePlugin.scala 359:28]
      end
      CsrPlugin_mcycle <= (CsrPlugin_mcycle + 64'h0000000000000001); // @[CsrPlugin.scala 1098:14]
      if(writeBack_arbitration_isFiring) begin
        CsrPlugin_minstret <= (CsrPlugin_minstret + 64'h0000000000000001); // @[CsrPlugin.scala 1100:18]
      end
      CsrPlugin_interrupt_valid <= 1'b0; // @[Reg.scala 39:30]
      if(when_CsrPlugin_l1218) begin
        if(when_CsrPlugin_l1224) begin
          CsrPlugin_interrupt_valid <= 1'b1; // @[CsrPlugin.scala 1225:23]
        end
        if(when_CsrPlugin_l1224_1) begin
          CsrPlugin_interrupt_valid <= 1'b1; // @[CsrPlugin.scala 1225:23]
        end
        if(when_CsrPlugin_l1224_2) begin
          CsrPlugin_interrupt_valid <= 1'b1; // @[CsrPlugin.scala 1225:23]
        end
      end
      if(CsrPlugin_pipelineLiberator_active) begin
        if(when_CsrPlugin_l1257) begin
          CsrPlugin_pipelineLiberator_pcValids_0 <= 1'b1; // @[CsrPlugin.scala 1258:19]
        end
        if(when_CsrPlugin_l1257_1) begin
          CsrPlugin_pipelineLiberator_pcValids_1 <= CsrPlugin_pipelineLiberator_pcValids_0; // @[CsrPlugin.scala 1258:19]
        end
        if(when_CsrPlugin_l1257_2) begin
          CsrPlugin_pipelineLiberator_pcValids_2 <= CsrPlugin_pipelineLiberator_pcValids_1; // @[CsrPlugin.scala 1258:19]
        end
      end
      if(when_CsrPlugin_l1262) begin
        CsrPlugin_pipelineLiberator_pcValids_0 <= 1'b0; // @[CsrPlugin.scala 1263:30]
        CsrPlugin_pipelineLiberator_pcValids_1 <= 1'b0; // @[CsrPlugin.scala 1263:30]
        CsrPlugin_pipelineLiberator_pcValids_2 <= 1'b0; // @[CsrPlugin.scala 1263:30]
      end
      if(CsrPlugin_interruptJump) begin
        CsrPlugin_interrupt_valid <= 1'b0; // @[CsrPlugin.scala 1274:46]
      end
      CsrPlugin_hadException <= CsrPlugin_exception; // @[Reg.scala 39:30]
      if(when_CsrPlugin_l1312) begin
        if(when_CsrPlugin_l1320) begin
          case(CsrPlugin_targetPrivilege)
            2'b11 : begin
              CsrPlugin_mstatus_MIE <= 1'b0; // @[CsrPlugin.scala 1336:28]
              CsrPlugin_mstatus_MPIE <= CsrPlugin_mstatus_MIE; // @[CsrPlugin.scala 1337:28]
              CsrPlugin_mstatus_MPP <= CsrPlugin_privilege; // @[CsrPlugin.scala 1338:28]
            end
            default : begin
            end
          endcase
        end
      end
      if(when_CsrPlugin_l1378) begin
        case(switch_CsrPlugin_l1382)
          2'b11 : begin
            CsrPlugin_mstatus_MPP <= 2'b00; // @[CsrPlugin.scala 1384:27]
            CsrPlugin_mstatus_MIE <= CsrPlugin_mstatus_MPIE; // @[CsrPlugin.scala 1385:27]
            CsrPlugin_mstatus_MPIE <= 1'b1; // @[CsrPlugin.scala 1386:28]
          end
          default : begin
          end
        endcase
      end
      execute_CsrPlugin_wfiWake <= (({_zz_when_CsrPlugin_l1224_2,{_zz_when_CsrPlugin_l1224_1,_zz_when_CsrPlugin_l1224}} != 3'b000) || CsrPlugin_thirdPartyWake); // @[Reg.scala 39:30]
      _zz_2 <= 1'b0; // @[Reg.scala 39:30]
      if(when_ShiftPlugins_l169) begin
        if(when_ShiftPlugins_l175) begin
          execute_LightShifterPlugin_isActive <= 1'b1; // @[ShiftPlugins.scala 176:20]
          if(execute_LightShifterPlugin_done) begin
            execute_LightShifterPlugin_isActive <= 1'b0; // @[ShiftPlugins.scala 180:22]
          end
        end
      end
      if(execute_arbitration_removeIt) begin
        execute_LightShifterPlugin_isActive <= 1'b0; // @[ShiftPlugins.scala 189:18]
      end
      HazardSimplePlugin_writeBackBuffer_valid <= HazardSimplePlugin_writeBackWrites_valid; // @[Reg.scala 39:30]
      if(when_Pipeline_l151) begin
        execute_arbitration_isValid <= 1'b0; // @[Pipeline.scala 152:35]
      end
      if(when_Pipeline_l154) begin
        execute_arbitration_isValid <= decode_arbitration_isValid; // @[Pipeline.scala 155:35]
      end
      if(when_Pipeline_l151_1) begin
        memory_arbitration_isValid <= 1'b0; // @[Pipeline.scala 152:35]
      end
      if(when_Pipeline_l154_1) begin
        memory_arbitration_isValid <= execute_arbitration_isValid; // @[Pipeline.scala 155:35]
      end
      if(when_Pipeline_l151_2) begin
        writeBack_arbitration_isValid <= 1'b0; // @[Pipeline.scala 152:35]
      end
      if(when_Pipeline_l154_2) begin
        writeBack_arbitration_isValid <= memory_arbitration_isValid; // @[Pipeline.scala 155:35]
      end
      if(execute_CsrPlugin_csr_768) begin
        if(execute_CsrPlugin_writeEnable) begin
          CsrPlugin_mstatus_MPIE <= CsrPlugin_csrMapping_writeDataSignal[7]; // @[Bool.scala 189:10]
          CsrPlugin_mstatus_MIE <= CsrPlugin_csrMapping_writeDataSignal[3]; // @[Bool.scala 189:10]
          case(switch_CsrPlugin_l982)
            2'b11 : begin
              CsrPlugin_mstatus_MPP <= 2'b11; // @[CsrPlugin.scala 983:30]
            end
            default : begin
            end
          endcase
        end
      end
      if(execute_CsrPlugin_csr_772) begin
        if(execute_CsrPlugin_writeEnable) begin
          CsrPlugin_mie_MEIE <= CsrPlugin_csrMapping_writeDataSignal[11]; // @[Bool.scala 189:10]
          CsrPlugin_mie_MTIE <= CsrPlugin_csrMapping_writeDataSignal[7]; // @[Bool.scala 189:10]
          CsrPlugin_mie_MSIE <= CsrPlugin_csrMapping_writeDataSignal[3]; // @[Bool.scala 189:10]
        end
      end
    end
  end

  always @(posedge clk) begin
    if(IBusSimplePlugin_iBusRsp_output_ready) begin
      _zz_IBusSimplePlugin_injector_decodeInput_payload_pc <= IBusSimplePlugin_iBusRsp_output_payload_pc; // @[Misc.scala 155:15]
      _zz_IBusSimplePlugin_injector_decodeInput_payload_rsp_error <= IBusSimplePlugin_iBusRsp_output_payload_rsp_error; // @[Misc.scala 155:15]
      _zz_IBusSimplePlugin_injector_decodeInput_payload_rsp_inst <= IBusSimplePlugin_iBusRsp_output_payload_rsp_inst; // @[Misc.scala 155:15]
      _zz_IBusSimplePlugin_injector_decodeInput_payload_isRvc <= IBusSimplePlugin_iBusRsp_output_payload_isRvc; // @[Misc.scala 155:15]
    end
    if(IBusSimplePlugin_injector_decodeInput_ready) begin
      IBusSimplePlugin_injector_formal_rawInDecode <= IBusSimplePlugin_iBusRsp_output_payload_rsp_inst; // @[Utils.scala 1084:26]
    end
    if(IBusSimplePlugin_cmd_ready) begin
      IBusSimplePlugin_cmd_rData_pc <= IBusSimplePlugin_cmd_payload_pc; // @[Stream.scala 378:28]
    end
    CsrPlugin_mip_MEIP <= externalInterrupt; // @[Reg.scala 39:30]
    CsrPlugin_mip_MTIP <= timerInterrupt; // @[Reg.scala 39:30]
    CsrPlugin_mip_MSIP <= softwareInterrupt; // @[Reg.scala 39:30]
    if(when_CsrPlugin_l1218) begin
      if(when_CsrPlugin_l1224) begin
        CsrPlugin_interrupt_code <= 4'b0111; // @[CsrPlugin.scala 1226:22]
        CsrPlugin_interrupt_targetPrivilege <= 2'b11; // @[CsrPlugin.scala 1227:33]
      end
      if(when_CsrPlugin_l1224_1) begin
        CsrPlugin_interrupt_code <= 4'b0011; // @[CsrPlugin.scala 1226:22]
        CsrPlugin_interrupt_targetPrivilege <= 2'b11; // @[CsrPlugin.scala 1227:33]
      end
      if(when_CsrPlugin_l1224_2) begin
        CsrPlugin_interrupt_code <= 4'b1011; // @[CsrPlugin.scala 1226:22]
        CsrPlugin_interrupt_targetPrivilege <= 2'b11; // @[CsrPlugin.scala 1227:33]
      end
    end
    if(when_CsrPlugin_l1312) begin
      if(when_CsrPlugin_l1320) begin
        case(CsrPlugin_targetPrivilege)
          2'b11 : begin
            CsrPlugin_mcause_interrupt <= (! CsrPlugin_hadException); // @[CsrPlugin.scala 1339:32]
            CsrPlugin_mcause_exceptionCode <= CsrPlugin_trapCause; // @[CsrPlugin.scala 1340:36]
            CsrPlugin_mepc <= decode_PC; // @[CsrPlugin.scala 1341:20]
          end
          default : begin
          end
        endcase
      end
    end
    if(when_ShiftPlugins_l169) begin
      if(when_ShiftPlugins_l175) begin
        execute_LightShifterPlugin_amplitudeReg <= (execute_LightShifterPlugin_amplitude - 5'h01); // @[ShiftPlugins.scala 177:24]
      end
    end
    HazardSimplePlugin_writeBackBuffer_payload_address <= HazardSimplePlugin_writeBackWrites_payload_address; // @[Reg.scala 39:30]
    HazardSimplePlugin_writeBackBuffer_payload_data <= HazardSimplePlugin_writeBackWrites_payload_data; // @[Reg.scala 39:30]
    if(when_Pipeline_l124) begin
      decode_to_execute_PC <= _zz_decode_to_execute_PC; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_1) begin
      execute_to_memory_PC <= execute_PC; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_2) begin
      memory_to_writeBack_PC <= memory_PC; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_3) begin
      decode_to_execute_INSTRUCTION <= decode_INSTRUCTION; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_4) begin
      execute_to_memory_INSTRUCTION <= execute_INSTRUCTION; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_5) begin
      memory_to_writeBack_INSTRUCTION <= memory_INSTRUCTION; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_6) begin
      decode_to_execute_FORMAL_PC_NEXT <= decode_FORMAL_PC_NEXT; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_7) begin
      execute_to_memory_FORMAL_PC_NEXT <= execute_FORMAL_PC_NEXT; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_8) begin
      memory_to_writeBack_FORMAL_PC_NEXT <= _zz_memory_to_writeBack_FORMAL_PC_NEXT; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_9) begin
      decode_to_execute_CSR_WRITE_OPCODE <= decode_CSR_WRITE_OPCODE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_10) begin
      decode_to_execute_CSR_READ_OPCODE <= decode_CSR_READ_OPCODE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_11) begin
      decode_to_execute_SRC_USE_SUB_LESS <= decode_SRC_USE_SUB_LESS; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_12) begin
      decode_to_execute_MEMORY_ENABLE <= decode_MEMORY_ENABLE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_13) begin
      execute_to_memory_MEMORY_ENABLE <= execute_MEMORY_ENABLE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_14) begin
      memory_to_writeBack_MEMORY_ENABLE <= memory_MEMORY_ENABLE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_15) begin
      decode_to_execute_REGFILE_WRITE_VALID <= decode_REGFILE_WRITE_VALID; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_16) begin
      execute_to_memory_REGFILE_WRITE_VALID <= execute_REGFILE_WRITE_VALID; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_17) begin
      memory_to_writeBack_REGFILE_WRITE_VALID <= memory_REGFILE_WRITE_VALID; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_18) begin
      decode_to_execute_BYPASSABLE_EXECUTE_STAGE <= decode_BYPASSABLE_EXECUTE_STAGE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_19) begin
      decode_to_execute_BYPASSABLE_MEMORY_STAGE <= decode_BYPASSABLE_MEMORY_STAGE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_20) begin
      execute_to_memory_BYPASSABLE_MEMORY_STAGE <= execute_BYPASSABLE_MEMORY_STAGE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_21) begin
      decode_to_execute_MEMORY_STORE <= decode_MEMORY_STORE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_22) begin
      execute_to_memory_MEMORY_STORE <= execute_MEMORY_STORE; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_23) begin
      decode_to_execute_IS_CSR <= decode_IS_CSR; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_24) begin
      decode_to_execute_ENV_CTRL <= _zz_decode_to_execute_ENV_CTRL; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_25) begin
      execute_to_memory_ENV_CTRL <= _zz_execute_to_memory_ENV_CTRL; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_26) begin
      memory_to_writeBack_ENV_CTRL <= _zz_memory_to_writeBack_ENV_CTRL; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_27) begin
      decode_to_execute_ALU_CTRL <= _zz_decode_to_execute_ALU_CTRL; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_28) begin
      decode_to_execute_SRC_LESS_UNSIGNED <= decode_SRC_LESS_UNSIGNED; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_29) begin
      decode_to_execute_ALU_BITWISE_CTRL <= _zz_decode_to_execute_ALU_BITWISE_CTRL; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_30) begin
      decode_to_execute_SHIFT_CTRL <= _zz_decode_to_execute_SHIFT_CTRL; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_31) begin
      decode_to_execute_BRANCH_CTRL <= _zz_decode_to_execute_BRANCH_CTRL; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_32) begin
      decode_to_execute_RS1 <= _zz_decode_to_execute_RS1; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_33) begin
      decode_to_execute_RS2 <= _zz_decode_to_execute_RS2; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_34) begin
      decode_to_execute_SRC2_FORCE_ZERO <= decode_SRC2_FORCE_ZERO; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_35) begin
      decode_to_execute_SRC1 <= decode_SRC1; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_36) begin
      decode_to_execute_SRC2 <= decode_SRC2; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_37) begin
      execute_to_memory_MEMORY_ADDRESS_LOW <= execute_MEMORY_ADDRESS_LOW; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_38) begin
      memory_to_writeBack_MEMORY_ADDRESS_LOW <= memory_MEMORY_ADDRESS_LOW; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_39) begin
      execute_to_memory_REGFILE_WRITE_DATA <= _zz_execute_to_memory_REGFILE_WRITE_DATA; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_40) begin
      memory_to_writeBack_REGFILE_WRITE_DATA <= memory_REGFILE_WRITE_DATA; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_41) begin
      execute_to_memory_BRANCH_DO <= execute_BRANCH_DO; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_42) begin
      execute_to_memory_BRANCH_CALC <= execute_BRANCH_CALC; // @[Pipeline.scala 124:40]
    end
    if(when_Pipeline_l124_43) begin
      memory_to_writeBack_MEMORY_READ_DATA <= memory_MEMORY_READ_DATA; // @[Pipeline.scala 124:40]
    end
    if(when_CsrPlugin_l1591) begin
      execute_CsrPlugin_csr_768 <= (decode_INSTRUCTION[31 : 20] == 12'h300); // @[CsrPlugin.scala 1591:101]
    end
    if(when_CsrPlugin_l1591_1) begin
      execute_CsrPlugin_csr_836 <= (decode_INSTRUCTION[31 : 20] == 12'h344); // @[CsrPlugin.scala 1591:101]
    end
    if(when_CsrPlugin_l1591_2) begin
      execute_CsrPlugin_csr_772 <= (decode_INSTRUCTION[31 : 20] == 12'h304); // @[CsrPlugin.scala 1591:101]
    end
    if(when_CsrPlugin_l1591_3) begin
      execute_CsrPlugin_csr_834 <= (decode_INSTRUCTION[31 : 20] == 12'h342); // @[CsrPlugin.scala 1591:101]
    end
    if(execute_CsrPlugin_csr_836) begin
      if(execute_CsrPlugin_writeEnable) begin
        CsrPlugin_mip_MSIP <= CsrPlugin_csrMapping_writeDataSignal[3]; // @[Bool.scala 189:10]
      end
    end
  end


endmodule

module StreamFifoLowLatency (
  input               io_push_valid,
  output              io_push_ready,
  input               io_push_payload_error,
  input      [31:0]   io_push_payload_inst,
  output reg          io_pop_valid,
  input               io_pop_ready,
  output reg          io_pop_payload_error,
  output reg [31:0]   io_pop_payload_inst,
  input               io_flush,
  output     [0:0]    io_occupancy,
  input               clk,
  input               reset
);

  reg                 when_Phase_l648;
  reg                 pushPtr_willIncrement;
  reg                 pushPtr_willClear;
  wire                pushPtr_willOverflowIfInc;
  wire                pushPtr_willOverflow;
  reg                 popPtr_willIncrement;
  reg                 popPtr_willClear;
  wire                popPtr_willOverflowIfInc;
  wire                popPtr_willOverflow;
  wire                ptrMatch;
  reg                 risingOccupancy;
  wire                empty;
  wire                full;
  wire                pushing;
  wire                popping;
  wire                readed_error;
  wire       [31:0]   readed_inst;
  wire       [32:0]   _zz_readed_error;
  wire                when_Stream_l1196;
  wire                when_Stream_l1209;
  wire       [32:0]   _zz_readed_error_1;
  reg        [32:0]   _zz_readed_error_2;

  always @(*) begin
    when_Phase_l648 = 1'b0; // @[when.scala 47:16]
    if(pushing) begin
      when_Phase_l648 = 1'b1; // @[when.scala 52:10]
    end
  end

  always @(*) begin
    pushPtr_willIncrement = 1'b0; // @[Utils.scala 536:23]
    if(pushing) begin
      pushPtr_willIncrement = 1'b1; // @[Utils.scala 540:41]
    end
  end

  always @(*) begin
    pushPtr_willClear = 1'b0; // @[Utils.scala 537:19]
    if(io_flush) begin
      pushPtr_willClear = 1'b1; // @[Utils.scala 539:33]
    end
  end

  assign pushPtr_willOverflowIfInc = 1'b1; // @[BaseType.scala 305:24]
  assign pushPtr_willOverflow = (pushPtr_willOverflowIfInc && pushPtr_willIncrement); // @[BaseType.scala 305:24]
  always @(*) begin
    popPtr_willIncrement = 1'b0; // @[Utils.scala 536:23]
    if(popping) begin
      popPtr_willIncrement = 1'b1; // @[Utils.scala 540:41]
    end
  end

  always @(*) begin
    popPtr_willClear = 1'b0; // @[Utils.scala 537:19]
    if(io_flush) begin
      popPtr_willClear = 1'b1; // @[Utils.scala 539:33]
    end
  end

  assign popPtr_willOverflowIfInc = 1'b1; // @[BaseType.scala 305:24]
  assign popPtr_willOverflow = (popPtr_willOverflowIfInc && popPtr_willIncrement); // @[BaseType.scala 305:24]
  assign ptrMatch = 1'b1; // @[BaseType.scala 305:24]
  assign empty = (ptrMatch && (! risingOccupancy)); // @[BaseType.scala 305:24]
  assign full = (ptrMatch && risingOccupancy); // @[BaseType.scala 305:24]
  assign pushing = (io_push_valid && io_push_ready); // @[BaseType.scala 305:24]
  assign popping = (io_pop_valid && io_pop_ready); // @[BaseType.scala 305:24]
  assign io_push_ready = (! full); // @[Stream.scala 1190:17]
  assign _zz_readed_error = _zz_readed_error_1; // @[Mem.scala 285:24]
  assign readed_error = _zz_readed_error[0]; // @[Bool.scala 189:10]
  assign readed_inst = _zz_readed_error[32 : 1]; // @[Bits.scala 133:56]
  assign when_Stream_l1196 = (! empty); // @[BaseType.scala 299:24]
  always @(*) begin
    if(when_Stream_l1196) begin
      io_pop_valid = 1'b1; // @[Stream.scala 1197:22]
    end else begin
      io_pop_valid = io_push_valid; // @[Stream.scala 1200:22]
    end
  end

  always @(*) begin
    if(when_Stream_l1196) begin
      io_pop_payload_error = readed_error; // @[Stream.scala 1198:24]
    end else begin
      io_pop_payload_error = io_push_payload_error; // @[Stream.scala 1201:24]
    end
  end

  always @(*) begin
    if(when_Stream_l1196) begin
      io_pop_payload_inst = readed_inst; // @[Stream.scala 1198:24]
    end else begin
      io_pop_payload_inst = io_push_payload_inst; // @[Stream.scala 1201:24]
    end
  end

  assign when_Stream_l1209 = (pushing != popping); // @[BaseType.scala 305:24]
  assign io_occupancy = (risingOccupancy && ptrMatch); // @[Stream.scala 1225:18]
  assign _zz_readed_error_1 = _zz_readed_error_2; // @[Phase.scala 647:23]
  always @(posedge clk) begin
    if(reset) begin
      risingOccupancy <= 1'b0; // @[Data.scala 400:33]
    end else begin
      if(when_Stream_l1209) begin
        risingOccupancy <= pushing; // @[Stream.scala 1210:21]
      end
      if(io_flush) begin
        risingOccupancy <= 1'b0; // @[Stream.scala 1237:21]
      end
    end
  end

  always @(posedge clk) begin
    if(when_Phase_l648) begin
      _zz_readed_error_2 <= {io_push_payload_inst,io_push_payload_error}; // @[Phase.scala 650:27]
    end
  end


endmodule
